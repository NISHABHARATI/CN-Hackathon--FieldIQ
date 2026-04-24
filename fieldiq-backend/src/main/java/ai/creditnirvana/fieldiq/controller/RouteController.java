package ai.creditnirvana.fieldiq.controller;

import ai.creditnirvana.fieldiq.dto.FieldVisitDTO;
import ai.creditnirvana.fieldiq.dto.ReoptimiseRequest;
import ai.creditnirvana.fieldiq.dto.RouteGenerationRequest;
import ai.creditnirvana.fieldiq.service.FieldVisitService;
import ai.creditnirvana.fieldiq.service.RouteOptimisationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteOptimisationService routeOptimisationService;
    private final FieldVisitService fieldVisitService;

    public RouteController(RouteOptimisationService routeOptimisationService, FieldVisitService fieldVisitService) {
        this.routeOptimisationService = routeOptimisationService;
        this.fieldVisitService = fieldVisitService;
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateRoutes(@RequestBody(required = false) RouteGenerationRequest req) {
        LocalDate date = (req != null && req.getDate() != null) ? req.getDate() : LocalDate.now();
        String city = (req != null) ? req.getCity() : null;
        return ResponseEntity.ok(routeOptimisationService.generateDailyRoutes(date, city));
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<FieldVisitDTO>> getAgentRoute(
            @PathVariable Long agentId,
            @RequestParam(required = false) String date) {
        LocalDate localDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return ResponseEntity.ok(fieldVisitService.getVisitsForAgent(agentId, localDate));
    }

    @GetMapping("/today")
    public ResponseEntity<List<FieldVisitDTO>> getAllRoutesToday(
            @RequestParam(required = false) String date) {
        LocalDate localDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return ResponseEntity.ok(fieldVisitService.getAllVisitsForDate(localDate));
    }

    /**
     * Mid-route reoptimisation: borrower unavailable, defer their visit to end of day
     * and re-sequence remaining stops from agent's current GPS position.
     *
     * Body: { agentId, date, deferVisitId, currentLat, currentLng }
     * Returns the full updated visit list for the agent, sorted by new routeOrder.
     */
    @PutMapping("/reoptimise")
    public ResponseEntity<List<FieldVisitDTO>> reoptimiseRoute(@RequestBody ReoptimiseRequest req) {
        LocalDate date        = req.getDate() != null ? req.getDate() : LocalDate.now();
        LocalTime windowStart = req.getVisitWindowStart() != null ? LocalTime.parse(req.getVisitWindowStart()) : null;
        LocalTime windowEnd   = req.getVisitWindowEnd()   != null ? LocalTime.parse(req.getVisitWindowEnd())   : null;
        return ResponseEntity.ok(routeOptimisationService.reoptimiseRoute(
                req.getAgentId(), date, req.getDeferVisitId(),
                req.getCurrentLat(), req.getCurrentLng(),
                windowStart, windowEnd));
    }

    /**
     * Compares Nearest-Neighbour vs Simulated Annealing without saving routes to DB.
     * Useful for demonstrating the algorithm improvement at the hackathon.
     */
    @PostMapping("/compare")
    public ResponseEntity<Map<String, Object>> compareAlgorithms(
            @RequestBody(required = false) RouteGenerationRequest req) {
        LocalDate date = (req != null && req.getDate() != null) ? req.getDate() : LocalDate.now();
        String city    = (req != null) ? req.getCity() : null;
        return ResponseEntity.ok(routeOptimisationService.compareAlgorithms(date, city));
    }
}