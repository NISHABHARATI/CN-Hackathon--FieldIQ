package ai.creditnirvana.fieldiq.service;

import ai.creditnirvana.fieldiq.dto.FieldVisitDTO;
import ai.creditnirvana.fieldiq.dto.PrioritisedAccountDTO;
import ai.creditnirvana.fieldiq.entity.Account;
import ai.creditnirvana.fieldiq.entity.FieldAgent;
import ai.creditnirvana.fieldiq.entity.FieldVisit;
import ai.creditnirvana.fieldiq.enums.VisitStatus;
import ai.creditnirvana.fieldiq.repository.AccountRepository;
import ai.creditnirvana.fieldiq.repository.FieldAgentRepository;
import ai.creditnirvana.fieldiq.repository.FieldVisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteOptimisationService {

    private static final Logger log = LoggerFactory.getLogger(RouteOptimisationService.class);

    private static final double AVG_SPEED_KMH      = 25.0;
    private static final double VISIT_DURATION_MIN = 15.0; // average time spent per visit (contact + docs)

    // ── Layer 2: Agent-Account Matching Score weights ─────────────────────────
    private static final double WEIGHT_LANGUAGE          = 0.25;
    private static final double WEIGHT_LOCAL_KNOWLEDGE   = 0.20;
    private static final double WEIGHT_PAST_SUCCESS      = 0.30;
    private static final double WEIGHT_PROXIMITY         = 0.15;
    private static final double WEIGHT_GENDER_SENSITIVITY = 0.10;
    private static final double MAX_PROXIMITY_KM         = 20.0;

    // ── Layer 3: Simulated Annealing parameters ───────────────────────────────
    private static final int    SA_MAX_ITERATIONS = 5000;
    private static final double SA_COOLING_RATE   = 0.995;

    private final AccountPrioritisationService prioritisationService;
    private final FieldAgentRepository agentRepository;
    private final AccountRepository accountRepository;
    private final FieldVisitRepository visitRepository;
    private final FieldVisitService fieldVisitService;

    public RouteOptimisationService(AccountPrioritisationService prioritisationService,
                                    FieldAgentRepository agentRepository,
                                    AccountRepository accountRepository,
                                    FieldVisitRepository visitRepository,
                                    FieldVisitService fieldVisitService) {
        this.prioritisationService = prioritisationService;
        this.agentRepository = agentRepository;
        this.accountRepository = accountRepository;
        this.visitRepository = visitRepository;
        this.fieldVisitService = fieldVisitService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Map<String, Object> generateDailyRoutes(LocalDate date, String city) {
        List<FieldAgent> agents = fetchAgents(city);
        if (agents.isEmpty()) {
            return Map.of("message", "No active agents found", "visitCount", 0);
        }

        List<Account> candidateAccounts = fetchCandidates(city, date);
        Map<FieldAgent, List<Account>> assignments = assignAccountsWithScoring(agents, candidateAccounts);

        List<FieldVisit> allVisits = new ArrayList<>();
        for (Map.Entry<FieldAgent, List<Account>> entry : assignments.entrySet()) {
            FieldAgent agent = entry.getKey();
            List<Account> agentAccounts = entry.getValue();

            // SA for geographic optimisation, then enforce business constraints
            List<Account> optimisedRoute = simulatedAnnealingRoute(agent, agentAccounts);
            optimisedRoute = applyConstraints(optimisedRoute);

            double prevLat = agent.getHomeLatitude() != null ? agent.getHomeLatitude()
                    : optimisedRoute.get(0).getLatitude();
            double prevLng = agent.getHomeLongitude() != null ? agent.getHomeLongitude()
                    : optimisedRoute.get(0).getLongitude();

            for (int i = 0; i < optimisedRoute.size(); i++) {
                Account account = optimisedRoute.get(i);
                double distKm = haversine(prevLat, prevLng, account.getLatitude(), account.getLongitude());
                double travelMinutes = (distKm / AVG_SPEED_KMH) * 60;

                PrioritisedAccountDTO scored = prioritisationService.score(account);
                double matchScore = calculateMatchingScore(agent, account);

                FieldVisit visit = FieldVisit.builder()
                        .account(account)
                        .agent(agent)
                        .scheduledDate(date)
                        .routeOrder(i + 1)
                        .status(VisitStatus.PENDING)
                        .priorityScore(scored.getPriorityScore())
                        .triggers(scored.getTriggers())
                        .distanceFromPrevious(Math.round(distKm * 100.0) / 100.0)
                        .estimatedTravelMinutes(Math.round(travelMinutes * 10.0) / 10.0)
                        .agentMatchScore(Math.round(matchScore * 1000.0) / 1000.0)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                allVisits.add(visit);
                prevLat = account.getLatitude();
                prevLng = account.getLongitude();
            }
        }

        visitRepository.saveAll(allVisits);

        long agentsWithVisits = allVisits.stream()
                .map(v -> v.getAgent().getId()).distinct().count();
        log.info("Generated {} visits across {} agents for {} (SA + constraint enforcement)",
                allVisits.size(), agentsWithVisits, date);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", date.toString());
        result.put("agentsAssigned", agentsWithVisits);
        result.put("visitCount", allVisits.size());
        result.put("accountsCovered", allVisits.size());
        result.put("accountsEligible", candidateAccounts.size());
        result.put("algorithm", "SimulatedAnnealing+ConstraintEnforcement");
        return result;
    }

    /**
     * Compares Nearest-Neighbour vs Simulated Annealing without persisting to DB.
     * Returns total route distances and per-agent breakdown for both algorithms.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> compareAlgorithms(LocalDate date, String city) {
        List<FieldAgent> agents = fetchAgents(city);
        if (agents.isEmpty()) {
            return Map.of("message", "No active agents found");
        }

        List<Account> candidateAccounts = fetchCandidates(city, date);
        Map<FieldAgent, List<Account>> assignments = assignAccountsWithScoring(agents, candidateAccounts);

        double nnTotalDist = 0.0;
        Map<FieldAgent, Double> nnDistByAgent = new LinkedHashMap<>();

        long nnStart = System.currentTimeMillis();
        for (Map.Entry<FieldAgent, List<Account>> e : assignments.entrySet()) {
            if (!e.getValue().isEmpty()) {
                double d = totalRouteDistance(e.getKey(), nearestNeighbourRoute(e.getKey(), e.getValue()));
                nnDistByAgent.put(e.getKey(), d);
                nnTotalDist += d;
            }
        }
        long nnMs = System.currentTimeMillis() - nnStart;

        double saTotalDist = 0.0;
        List<Map<String, Object>> agentBreakdown = new ArrayList<>();

        long saStart = System.currentTimeMillis();
        for (Map.Entry<FieldAgent, List<Account>> e : assignments.entrySet()) {
            FieldAgent agent = e.getKey();
            List<Account> accts = e.getValue();
            if (accts.isEmpty()) continue;

            double nnDist = nnDistByAgent.getOrDefault(agent, 0.0);
            double saDist = totalRouteDistance(agent, simulatedAnnealingRoute(agent, accts));
            saTotalDist += saDist;

            double improvePct = nnDist > 0
                    ? Math.round(((nnDist - saDist) / nnDist) * 10000.0) / 100.0 : 0.0;

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("agentId", agent.getId());
            row.put("agentName", agent.getName());
            row.put("accountCount", accts.size());
            row.put("nnDistanceKm", Math.round(nnDist * 100.0) / 100.0);
            row.put("saDistanceKm", Math.round(saDist * 100.0) / 100.0);
            row.put("improvementPct", improvePct);
            agentBreakdown.add(row);
        }
        long saMs = System.currentTimeMillis() - saStart;

        double overallImprove = nnTotalDist > 0
                ? Math.round(((nnTotalDist - saTotalDist) / nnTotalDist) * 10000.0) / 100.0 : 0.0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", date.toString());
        result.put("accountsAssigned", candidateAccounts.size());
        result.put("nearestNeighbour", Map.of(
                "totalDistanceKm", Math.round(nnTotalDist * 100.0) / 100.0,
                "computeTimeMs", nnMs));
        result.put("simulatedAnnealing", Map.of(
                "totalDistanceKm", Math.round(saTotalDist * 100.0) / 100.0,
                "computeTimeMs", saMs));
        result.put("improvementPercent", overallImprove);
        result.put("agentBreakdown", agentBreakdown);
        return result;
    }

    /**
     * Mid-route reoptimisation triggered when a borrower is unavailable or requests a specific time.
     *
     * Two modes:
     *   1. No time window → defer to end of day (original behaviour).
     *   2. visitWindowStart + visitWindowEnd provided → walk the NN-ordered remaining stops,
     *      accumulate travel + visit time from now, and insert the deferred visit at the first
     *      position where the estimated arrival falls within the borrower's requested window.
     *      If no position fits, insert just before the first stop that would be reached AFTER
     *      windowStart (so the agent arrives as close to the window as possible).
     *
     * COMPLETED / SKIPPED visits are always left untouched.
     */
    @Transactional
    public List<FieldVisitDTO> reoptimiseRoute(Long agentId, LocalDate date,
                                               Long deferVisitId,
                                               Double currentLat, Double currentLng,
                                               LocalTime windowStart, LocalTime windowEnd) {
        agentRepository.findById(agentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agent not found: " + agentId));

        List<FieldVisit> allVisits = visitRepository.findByAgentIdAndScheduledDate(agentId, date);
        if (allVisits.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No visits found for agent on " + date);
        }

        List<FieldVisit> locked = allVisits.stream()
                .filter(v -> v.getStatus() == VisitStatus.COMPLETED || v.getStatus() == VisitStatus.SKIPPED)
                .collect(Collectors.toList());

        FieldVisit deferred = allVisits.stream()
                .filter(v -> v.getId().equals(deferVisitId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visit not found: " + deferVisitId));

        List<FieldVisit> toReoptimise = allVisits.stream()
                .filter(v -> v.getStatus() != VisitStatus.COMPLETED
                        && v.getStatus() != VisitStatus.SKIPPED
                        && !v.getId().equals(deferVisitId))
                .collect(Collectors.toList());

        int startOrder = locked.stream()
                .mapToInt(v -> v.getRouteOrder() != null ? v.getRouteOrder() : 0)
                .max().orElse(0) + 1;

        // Record borrower's requested window on the visit
        deferred.setVisitWindowStart(windowStart);
        deferred.setVisitWindowEnd(windowEnd);

        List<Account> remainingAccounts = toReoptimise.stream()
                .map(FieldVisit::getAccount).collect(Collectors.toList());

        List<Account> reordered = nearestNeighbourFromPosition(currentLat, currentLng, remainingAccounts);
        reordered = applyConstraints(reordered);

        // Find insertion index for the deferred visit based on time window
        int insertAt = findTimeWindowInsertionIndex(reordered, currentLat, currentLng, windowStart);

        // Build final ordered list: stops before insertion + deferred + stops after
        List<Account> before = reordered.subList(0, insertAt);
        List<Account> after  = reordered.subList(insertAt, reordered.size());

        List<FieldVisit> toSave = new ArrayList<>();
        double prevLat = currentLat, prevLng = currentLng;
        int order = startOrder;

        for (Account account : before) {
            FieldVisit visit = findVisitForAccount(toReoptimise, account);
            double dist = haversine(prevLat, prevLng, account.getLatitude(), account.getLongitude());
            visit.setRouteOrder(order++);
            visit.setDistanceFromPrevious(Math.round(dist * 100.0) / 100.0);
            visit.setEstimatedTravelMinutes(Math.round((dist / AVG_SPEED_KMH * 60) * 10.0) / 10.0);
            visit.setUpdatedAt(LocalDateTime.now());
            toSave.add(visit);
            prevLat = account.getLatitude();
            prevLng = account.getLongitude();
        }

        // Insert deferred visit at its time-window position
        Account dAcc = deferred.getAccount();
        double dDist = haversine(prevLat, prevLng, dAcc.getLatitude(), dAcc.getLongitude());
        deferred.setRouteOrder(order++);
        deferred.setDistanceFromPrevious(Math.round(dDist * 100.0) / 100.0);
        deferred.setEstimatedTravelMinutes(Math.round((dDist / AVG_SPEED_KMH * 60) * 10.0) / 10.0);
        deferred.setUpdatedAt(LocalDateTime.now());
        toSave.add(deferred);
        prevLat = dAcc.getLatitude();
        prevLng = dAcc.getLongitude();

        for (Account account : after) {
            FieldVisit visit = findVisitForAccount(toReoptimise, account);
            double dist = haversine(prevLat, prevLng, account.getLatitude(), account.getLongitude());
            visit.setRouteOrder(order++);
            visit.setDistanceFromPrevious(Math.round(dist * 100.0) / 100.0);
            visit.setEstimatedTravelMinutes(Math.round((dist / AVG_SPEED_KMH * 60) * 10.0) / 10.0);
            visit.setUpdatedAt(LocalDateTime.now());
            toSave.add(visit);
            prevLat = account.getLatitude();
            prevLng = account.getLongitude();
        }

        visitRepository.saveAll(toSave);
        log.info("Reoptimised route for agent {} on {}: visitId={} inserted at slot {} (window={}-{}), {} stops reordered",
                agentId, date, deferVisitId, deferred.getRouteOrder(), windowStart, windowEnd, toReoptimise.size());

        return visitRepository.findByAgentIdAndScheduledDate(agentId, date).stream()
                .sorted(Comparator.comparingInt(v -> v.getRouteOrder() != null ? v.getRouteOrder() : 0))
                .map(fieldVisitService::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Walks the NN-ordered remaining stops from (startLat, startLng), accumulating
     * travel time + VISIT_DURATION_MIN per stop, and returns the index at which the
     * deferred visit should be inserted so the agent arrives at or after windowStart.
     * Returns reordered.size() (append at end) if windowStart is null or after all stops.
     */
    private int findTimeWindowInsertionIndex(List<Account> reordered,
                                              double startLat, double startLng,
                                              LocalTime windowStart) {
        if (windowStart == null || reordered.isEmpty()) return reordered.size();

        LocalTime now = LocalTime.now();
        double cumulativeMinutes = 0.0;
        double prevLat = startLat, prevLng = startLng;

        for (int i = 0; i < reordered.size(); i++) {
            Account a = reordered.get(i);
            double dist     = haversine(prevLat, prevLng, a.getLatitude(), a.getLongitude());
            double travelMin = (dist / AVG_SPEED_KMH) * 60;
            cumulativeMinutes += travelMin + VISIT_DURATION_MIN;

            LocalTime estimatedArrival = now.plusMinutes((long) cumulativeMinutes);
            // Insert BEFORE this stop if, by the time we get here, we've passed the window start
            if (!estimatedArrival.isBefore(windowStart)) {
                return i;
            }
            prevLat = a.getLatitude();
            prevLng = a.getLongitude();
        }
        return reordered.size(); // window is after all remaining stops — append at end
    }

    private FieldVisit findVisitForAccount(List<FieldVisit> visits, Account account) {
        return visits.stream()
                .filter(v -> v.getAccount().getId().equals(account.getId()))
                .findFirst().orElseThrow();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Layer 2 — Agent-Account Matching Score
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Calculates a composite matching score (0.0 – 1.0) across five dimensions:
     *
     *   Dimension              Weight   Scoring logic
     *   ─────────────────────────────────────────────
     *   Language Match           25%    full if agent speaks borrower's preferred language
     *   Local Knowledge          20%    full if agent trained in the zone; 70% if home zone
     *   Past Success             30%    agent's historical success rate (default 50% for new)
     *   Location Proximity       15%    linear decay to 0 at MAX_PROXIMITY_KM
     *   Gender Sensitivity       10%    full if no requirement OR gender match; 0 if mismatch
     */
    double calculateMatchingScore(FieldAgent agent, Account account) {
        double score = 0.0;

        // 1. Language Match (25%)
        if (account.getPreferredLanguage() == null || account.getPreferredLanguage().isBlank()) {
            score += WEIGHT_LANGUAGE;
        } else if (agent.getLanguages() != null
                && agent.getLanguages().stream()
                        .anyMatch(l -> l.equalsIgnoreCase(account.getPreferredLanguage()))) {
            score += WEIGHT_LANGUAGE;
        }

        // 2. Local Knowledge (20%)
        if (account.getZone() == null) {
            score += WEIGHT_LOCAL_KNOWLEDGE;
        } else if (agent.getKnownZones() != null
                && agent.getKnownZones().stream()
                        .anyMatch(z -> z.equalsIgnoreCase(account.getZone()))) {
            score += WEIGHT_LOCAL_KNOWLEDGE;
        } else if (Objects.equals(agent.getZone(), account.getZone())) {
            score += WEIGHT_LOCAL_KNOWLEDGE * 0.7; // home zone — partial credit
        }

        // 3. Past Success (30%)
        int total   = agent.getTotalAssignedVisits() != null ? agent.getTotalAssignedVisits() : 0;
        int success = agent.getSuccessfulVisits()    != null ? agent.getSuccessfulVisits()    : 0;
        double successRate = total > 0 ? Math.min(1.0, (double) success / total) : 0.5;
        score += WEIGHT_PAST_SUCCESS * successRate;

        // 4. Location Proximity (15%)
        double agentLat = agent.getHomeLatitude()    != null ? agent.getHomeLatitude()
                        : (agent.getCurrentLatitude() != null ? agent.getCurrentLatitude()
                        : account.getLatitude());
        double agentLng = agent.getHomeLongitude()   != null ? agent.getHomeLongitude()
                        : (agent.getCurrentLongitude() != null ? agent.getCurrentLongitude()
                        : account.getLongitude());
        double dist = haversine(agentLat, agentLng, account.getLatitude(), account.getLongitude());
        score += WEIGHT_PROXIMITY * Math.max(0.0, 1.0 - (dist / MAX_PROXIMITY_KM));

        // 5. Gender Sensitivity (10%)
        if (!Boolean.TRUE.equals(account.getRequiresGenderSensitivity())) {
            score += WEIGHT_GENDER_SENSITIVITY;
        } else if (agent.getGender() != null && account.getBorrowerGender() != null
                && agent.getGender().equalsIgnoreCase(account.getBorrowerGender())) {
            score += WEIGHT_GENDER_SENSITIVITY;
        }

        return score;
    }

    /**
     * Assigns accounts to agents using the 5-dimension matching score.
     * Accounts arrive in priority order; each is given to the best-scoring
     * available agent (still within maxVisitsPerDay capacity).
     */
    private Map<FieldAgent, List<Account>> assignAccountsWithScoring(
            List<FieldAgent> agents, List<Account> accounts) {

        Map<FieldAgent, List<Account>> assignments = new LinkedHashMap<>();
        agents.forEach(a -> assignments.put(a, new ArrayList<>()));

        for (Account account : accounts) {
            agents.stream()
                    .filter(a -> assignments.get(a).size() < a.getMaxVisitsPerDay())
                    .max(Comparator.comparingDouble(a -> calculateMatchingScore(a, account)))
                    .ifPresent(best -> assignments.get(best).add(account));
        }

        assignments.entrySet().removeIf(e -> e.getValue().isEmpty());
        return assignments;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Layer 3 — Route Optimisation Algorithms
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Algorithm 1 — Greedy Nearest-Neighbour heuristic.
     * O(n²) time; fast but can miss globally better routes.
     */
    private List<Account> nearestNeighbourRoute(FieldAgent agent, List<Account> accounts) {
        if (accounts.isEmpty()) return Collections.emptyList();

        List<Account> unvisited = new ArrayList<>(accounts);
        List<Account> route = new ArrayList<>();

        double curLat = agent.getHomeLatitude()  != null ? agent.getHomeLatitude()  : accounts.get(0).getLatitude();
        double curLng = agent.getHomeLongitude() != null ? agent.getHomeLongitude() : accounts.get(0).getLongitude();

        while (!unvisited.isEmpty()) {
            final double fLat = curLat, fLng = curLng;
            Account nearest = unvisited.stream()
                    .min(Comparator.comparingDouble(a -> haversine(fLat, fLng, a.getLatitude(), a.getLongitude())))
                    .orElseThrow();
            route.add(nearest);
            unvisited.remove(nearest);
            curLat = nearest.getLatitude();
            curLng = nearest.getLongitude();
        }
        return route;
    }

    /** NN from an arbitrary (lat, lng) start — used for mid-route reoptimisation. */
    private List<Account> nearestNeighbourFromPosition(double startLat, double startLng, List<Account> accounts) {
        if (accounts.isEmpty()) return Collections.emptyList();
        List<Account> unvisited = new ArrayList<>(accounts);
        List<Account> route = new ArrayList<>();
        double curLat = startLat, curLng = startLng;
        while (!unvisited.isEmpty()) {
            final double fLat = curLat, fLng = curLng;
            Account nearest = unvisited.stream()
                    .min(Comparator.comparingDouble(a -> haversine(fLat, fLng, a.getLatitude(), a.getLongitude())))
                    .orElseThrow();
            route.add(nearest);
            unvisited.remove(nearest);
            curLat = nearest.getLatitude();
            curLng = nearest.getLongitude();
        }
        return route;
    }

    /**
     * Algorithm 2 — Simulated Annealing with 2-opt neighbourhood.
     * Starts from the NN solution and iteratively escapes local minima.
     * Typically improves total route distance by 5–20% over NN alone.
     */
    private List<Account> simulatedAnnealingRoute(FieldAgent agent, List<Account> accounts) {
        if (accounts.size() <= 3) return nearestNeighbourRoute(agent, accounts);

        List<Account> current = new ArrayList<>(nearestNeighbourRoute(agent, accounts));
        List<Account> best    = new ArrayList<>(current);
        double bestDist    = totalRouteDistance(agent, best);
        double currentDist = bestDist;

        // Initial temperature ≈ average edge length (proportional to problem scale)
        double temperature = bestDist / accounts.size();
        Random rng = new Random(42);

        for (int iter = 0; iter < SA_MAX_ITERATIONS && temperature > 1e-4; iter++) {
            int i = rng.nextInt(current.size());
            int j = rng.nextInt(current.size());
            if (i == j) continue;

            int left  = Math.min(i, j);
            int right = Math.max(i, j);
            List<Account> neighbor = twoOptSwap(current, left, right);
            double neighborDist = totalRouteDistance(agent, neighbor);
            double delta = neighborDist - currentDist;

            if (delta < 0 || rng.nextDouble() < Math.exp(-delta / temperature)) {
                current     = neighbor;
                currentDist = neighborDist;
                if (currentDist < bestDist) {
                    best     = new ArrayList<>(current);
                    bestDist = currentDist;
                }
            }
            temperature *= SA_COOLING_RATE;
        }
        return best;
    }

    private List<Account> twoOptSwap(List<Account> route, int i, int j) {
        List<Account> result = new ArrayList<>(route.subList(0, i));
        List<Account> seg = new ArrayList<>(route.subList(i, j + 1));
        Collections.reverse(seg);
        result.addAll(seg);
        result.addAll(route.subList(j + 1, route.size()));
        return result;
    }

    private double totalRouteDistance(FieldAgent agent, List<Account> route) {
        if (route.isEmpty()) return 0.0;
        double total  = 0.0;
        double prevLat = agent.getHomeLatitude()  != null ? agent.getHomeLatitude()  : route.get(0).getLatitude();
        double prevLng = agent.getHomeLongitude() != null ? agent.getHomeLongitude() : route.get(0).getLongitude();
        for (Account a : route) {
            total  += haversine(prevLat, prevLng, a.getLatitude(), a.getLongitude());
            prevLat = a.getLatitude();
            prevLng = a.getLongitude();
        }
        return total;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Configurable Constraints
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Applies three business constraints to a geographically optimised route:
     *
     * 1. Priority override  — Legal Notice accounts are always placed first.
     * 2. Time windows       — Salaried borrowers (typically home in the morning)
     *                         are scheduled before self-employed / business.
     * 3. Safety rule        — High-risk accounts must not occupy the last 2 slots
     *                         (avoids visits after ~16:00 without backup).
     */
    private List<Account> applyConstraints(List<Account> route) {
        if (route.size() <= 1) return route;

        // Partition
        List<Account> legalNotice = route.stream()
                .filter(a -> Boolean.TRUE.equals(a.getHasPendingLegalNotice()))
                .collect(Collectors.toList());
        List<Account> rest = route.stream()
                .filter(a -> !Boolean.TRUE.equals(a.getHasPendingLegalNotice()))
                .collect(Collectors.toList());

        List<Account> salaryMorning = rest.stream()
                .filter(a -> "SALARIED".equals(a.getBorrowerType()))
                .collect(Collectors.toList());
        List<Account> others = rest.stream()
                .filter(a -> !"SALARIED".equals(a.getBorrowerType()))
                .collect(Collectors.toList());

        List<Account> constrained = new ArrayList<>();
        constrained.addAll(legalNotice);    // rule 1: legal notices first
        constrained.addAll(salaryMorning);  // rule 2: salaried in morning slots
        constrained.addAll(others);

        // Rule 3: high-risk out of last 2 slots
        int n = constrained.size();
        if (n > 2) {
            for (int i = n - 2; i < n; i++) {
                if (Boolean.TRUE.equals(constrained.get(i).getIsHighRisk())) {
                    for (int j = i - 1; j >= 0; j--) {
                        if (!Boolean.TRUE.equals(constrained.get(j).getIsHighRisk())) {
                            Collections.swap(constrained, i, j);
                            break;
                        }
                    }
                }
            }
        }

        return constrained;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private List<FieldAgent> fetchAgents(String city) {
        return (city != null && !city.isBlank())
                ? agentRepository.findByCityAndIsActiveTrue(city)
                : agentRepository.findByIsActiveTrue();
    }

    private List<Account> fetchCandidates(String city, LocalDate date) {
        return prioritisationService.getPrioritisedAccounts(city).stream()
                .map(dto -> accountRepository.findById(dto.getId()).orElseThrow())
                .filter(a -> !visitRepository.existsByAccountIdAndScheduledDate(a.getId(), date))
                .collect(Collectors.toList());
    }

    /** Haversine formula — returns great-circle distance in kilometres. */
    public static double haversine(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}