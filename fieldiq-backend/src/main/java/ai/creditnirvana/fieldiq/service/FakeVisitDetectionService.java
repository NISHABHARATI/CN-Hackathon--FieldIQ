package ai.creditnirvana.fieldiq.service;

import ai.creditnirvana.fieldiq.dto.FraudFlagDTO;
import ai.creditnirvana.fieldiq.entity.FieldAgent;
import ai.creditnirvana.fieldiq.entity.FieldVisit;
import ai.creditnirvana.fieldiq.entity.VisitOutcome;
import ai.creditnirvana.fieldiq.enums.OutcomeType;
import ai.creditnirvana.fieldiq.enums.VisitStatus;
import ai.creditnirvana.fieldiq.repository.FieldAgentRepository;
import ai.creditnirvana.fieldiq.repository.FieldVisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Detects four patterns of fraudulent field-visit activity:
 *
 *  GPS_SPOOFING        — velocity between consecutive visits exceeds physical limits
 *  DISPOSITION_STUFFING — outcome logged as "contacted" but no GPS/evidence to support it
 *  DUPLICATE_VISIT     — same account_id completed twice on the same date (same agent)
 *  CLUSTER_FRAUD       — all GPS coordinates logged within a 500 m radius despite
 *                        many distinct account addresses
 */
@Service
public class FakeVisitDetectionService {

    private static final Logger log = LoggerFactory.getLogger(FakeVisitDetectionService.class);

    // Speed above which travel is physically implausible for a field agent on city roads
    private static final double GPS_SPOOF_SPEED_KMH     = 150.0;
    private static final double GPS_SUSPECT_SPEED_KMH   = 80.0;

    // Cluster-fraud threshold: all GPS within this radius counts as suspicious
    private static final double CLUSTER_RADIUS_KM       = 0.5;
    private static final int    MIN_CLUSTER_VISIT_COUNT  = 4;

    // Disposition stuffing: "contact" outcomes that need supporting evidence
    private static final Set<OutcomeType> CONTACT_OUTCOMES = Set.of(
            OutcomeType.PAID_FULL, OutcomeType.PAID_PARTIAL, OutcomeType.PTP,
            OutcomeType.REFUSED, OutcomeType.ALREADY_SETTLED,
            OutcomeType.CONTACT_LATER, OutcomeType.LEGAL_NOTICE_DELIVERED
    );

    private final FieldVisitRepository visitRepository;
    private final FieldAgentRepository agentRepository;

    public FakeVisitDetectionService(FieldVisitRepository visitRepository,
                                     FieldAgentRepository agentRepository) {
        this.visitRepository = visitRepository;
        this.agentRepository = agentRepository;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Runs all four fraud checks for a single agent on the given date.
     */
    @Transactional(readOnly = true)
    public List<FraudFlagDTO> analyzeAgent(Long agentId, LocalDate date) {
        FieldAgent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agent not found"));

        List<FieldVisit> allVisits = visitRepository.findByAgentIdAndScheduledDate(agentId, date);
        List<FieldVisit> completed = allVisits.stream()
                .filter(v -> v.getStatus() == VisitStatus.COMPLETED)
                .collect(Collectors.toList());

        List<FraudFlagDTO> flags = new ArrayList<>();
        flags.addAll(detectGpsSpoofing(completed, agent));
        flags.addAll(detectDispositionStuffing(completed, agent));
        flags.addAll(detectDuplicateVisits(allVisits, agent));
        flags.addAll(detectClusterFraud(completed, agent));

        if (!flags.isEmpty()) {
            log.warn("Fraud analysis for agent {} on {}: {} flag(s) raised",
                    agent.getName(), date, flags.size());
        }
        return flags;
    }

    /**
     * Runs all four fraud checks for every active agent on the given date.
     * Returns a map of agentId → list of flags (empty lists excluded).
     */
    @Transactional(readOnly = true)
    public Map<Long, List<FraudFlagDTO>> analyzeAllAgents(LocalDate date) {
        List<FieldAgent> agents = agentRepository.findByIsActiveTrue();
        Map<Long, List<FraudFlagDTO>> report = new LinkedHashMap<>();
        for (FieldAgent agent : agents) {
            List<FraudFlagDTO> flags = analyzeAgent(agent.getId(), date);
            if (!flags.isEmpty()) {
                report.put(agent.getId(), flags);
            }
        }
        return report;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Detection: GPS Spoofing
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Sorts completed visits by visitedAt and checks the speed between each
     * consecutive pair. A field agent cannot travel > 150 km/h in a city.
     */
    private List<FraudFlagDTO> detectGpsSpoofing(List<FieldVisit> completed, FieldAgent agent) {
        List<FraudFlagDTO> flags = new ArrayList<>();

        List<FieldVisit> withGps = completed.stream()
                .filter(v -> v.getOutcome() != null
                        && v.getOutcome().getVisitLatitude()  != null
                        && v.getOutcome().getVisitLongitude() != null
                        && v.getOutcome().getVisitedAt()      != null)
                .sorted(Comparator.comparing(v -> v.getOutcome().getVisitedAt()))
                .collect(Collectors.toList());

        for (int i = 1; i < withGps.size(); i++) {
            VisitOutcome prev = withGps.get(i - 1).getOutcome();
            VisitOutcome curr = withGps.get(i).getOutcome();

            long secondsElapsed = ChronoUnit.SECONDS.between(prev.getVisitedAt(), curr.getVisitedAt());
            if (secondsElapsed <= 0) continue;

            double distKm  = RouteOptimisationService.haversine(
                    prev.getVisitLatitude(), prev.getVisitLongitude(),
                    curr.getVisitLatitude(), curr.getVisitLongitude());
            double speedKmh = distKm / (secondsElapsed / 3600.0);

            if (speedKmh >= GPS_SPOOF_SPEED_KMH) {
                flags.add(new FraudFlagDTO(
                        "GPS_SPOOFING", "HIGH",
                        agent.getId(), agent.getName(),
                        curr.getVisit().getId(),
                        String.format("%.1f km travelled in %d s between visit #%d and #%d (%.0f km/h — physically impossible)",
                                distKm, secondsElapsed,
                                withGps.get(i - 1).getId(), withGps.get(i).getId(), speedKmh)));
            } else if (speedKmh >= GPS_SUSPECT_SPEED_KMH) {
                flags.add(new FraudFlagDTO(
                        "GPS_SPOOFING", "MEDIUM",
                        agent.getId(), agent.getName(),
                        curr.getVisit().getId(),
                        String.format("%.1f km in %d s between visit #%d and #%d (%.0f km/h — highly suspicious for city roads)",
                                distKm, secondsElapsed,
                                withGps.get(i - 1).getId(), withGps.get(i).getId(), speedKmh)));
            }
        }
        return flags;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Detection: Disposition Stuffing
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Flags completed visits where a "contact" outcome was logged but there is
     * no GPS coordinate, no evidence photo, and no meaningful notes —
     * indicating the agent may have fabricated the outcome without visiting.
     */
    private List<FraudFlagDTO> detectDispositionStuffing(List<FieldVisit> completed, FieldAgent agent) {
        List<FraudFlagDTO> flags = new ArrayList<>();

        for (FieldVisit visit : completed) {
            VisitOutcome outcome = visit.getOutcome();
            if (outcome == null) continue;
            if (!CONTACT_OUTCOMES.contains(outcome.getOutcome())) continue;

            boolean noGps      = outcome.getVisitLatitude() == null || outcome.getVisitLongitude() == null;
            boolean noEvidence = outcome.getEvidencePhotoUrl() == null || outcome.getEvidencePhotoUrl().isBlank();
            boolean noNotes    = outcome.getNotes() == null || outcome.getNotes().trim().length() < 5;

            if (noGps && noEvidence && noNotes) {
                flags.add(new FraudFlagDTO(
                        "DISPOSITION_STUFFING", "HIGH",
                        agent.getId(), agent.getName(),
                        visit.getId(),
                        String.format("Visit #%d logged outcome '%s' for account '%s' with no GPS, no evidence photo, and no notes",
                                visit.getId(), outcome.getOutcome(),
                                visit.getAccount().getBorrowerName())));
            } else if (noGps && (noEvidence || noNotes)) {
                flags.add(new FraudFlagDTO(
                        "DISPOSITION_STUFFING", "MEDIUM",
                        agent.getId(), agent.getName(),
                        visit.getId(),
                        String.format("Visit #%d logged outcome '%s' without GPS and with incomplete supporting evidence",
                                visit.getId(), outcome.getOutcome())));
            }
        }
        return flags;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Detection: Duplicate Visits
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Detects when the same account appears more than once in an agent's
     * schedule for the same date — either submitted twice or double-booked.
     */
    private List<FraudFlagDTO> detectDuplicateVisits(List<FieldVisit> allVisits, FieldAgent agent) {
        List<FraudFlagDTO> flags = new ArrayList<>();

        Map<Long, List<FieldVisit>> byAccount = allVisits.stream()
                .collect(Collectors.groupingBy(v -> v.getAccount().getId()));

        byAccount.forEach((accountId, visits) -> {
            if (visits.size() > 1) {
                List<Long> visitIds = visits.stream().map(FieldVisit::getId).collect(Collectors.toList());
                String accountName = visits.get(0).getAccount().getBorrowerName();
                // Flag each duplicate beyond the first
                for (int i = 1; i < visits.size(); i++) {
                    flags.add(new FraudFlagDTO(
                            "DUPLICATE_VISIT", "HIGH",
                            agent.getId(), agent.getName(),
                            visits.get(i).getId(),
                            String.format("Account '%s' (id=%d) appears %d times in agent's schedule: visit IDs %s",
                                    accountName, accountId, visits.size(), visitIds)));
                }
            }
        });
        return flags;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Detection: Cluster Fraud
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Detects when an agent logs many visits but their GPS coordinates never
     * leave a 500 m radius — consistent with an agent submitting outcomes
     * from a single stationary location (e.g. a tea stall or home).
     */
    private List<FraudFlagDTO> detectClusterFraud(List<FieldVisit> completed, FieldAgent agent) {
        List<FraudFlagDTO> flags = new ArrayList<>();

        List<double[]> coords = completed.stream()
                .filter(v -> v.getOutcome() != null
                        && v.getOutcome().getVisitLatitude()  != null
                        && v.getOutcome().getVisitLongitude() != null)
                .map(v -> new double[]{
                        v.getOutcome().getVisitLatitude(),
                        v.getOutcome().getVisitLongitude(),
                        v.getId().doubleValue()})
                .collect(Collectors.toList());

        if (coords.size() < MIN_CLUSTER_VISIT_COUNT) return flags;

        double centroidLat = coords.stream().mapToDouble(c -> c[0]).average().orElse(0);
        double centroidLng = coords.stream().mapToDouble(c -> c[1]).average().orElse(0);

        double maxRadius = coords.stream()
                .mapToDouble(c -> RouteOptimisationService.haversine(centroidLat, centroidLng, c[0], c[1]))
                .max().orElse(0);

        if (maxRadius <= CLUSTER_RADIUS_KM) {
            List<Long> visitIds = coords.stream().map(c -> (long) c[2]).collect(Collectors.toList());
            flags.add(new FraudFlagDTO(
                    "CLUSTER_FRAUD", "HIGH",
                    agent.getId(), agent.getName(),
                    null,
                    String.format("%d visits logged but all GPS coordinates within %.0f m of centroid (%.6f, %.6f). "
                            + "Affected visit IDs: %s",
                            coords.size(), maxRadius * 1000, centroidLat, centroidLng, visitIds)));
        }
        return flags;
    }
}