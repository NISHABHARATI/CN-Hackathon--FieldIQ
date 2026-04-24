package ai.creditnirvana.fieldiq.controller;

import ai.creditnirvana.fieldiq.dto.FraudFlagDTO;
import ai.creditnirvana.fieldiq.service.FakeVisitDetectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Endpoints for real-time fake-visit fraud detection.
 *
 *   GET /api/fraud-detection/analyze?date=&agentId=   — analyze a single agent
 *   GET /api/fraud-detection/analyze?date=             — analyze all agents
 */
@RestController
@RequestMapping("/api/fraud-detection")
public class FakeVisitDetectionController {

    private final FakeVisitDetectionService detectionService;

    public FakeVisitDetectionController(FakeVisitDetectionService detectionService) {
        this.detectionService = detectionService;
    }

    /**
     * When agentId is provided, returns fraud flags for that agent on the given date.
     * When agentId is omitted, returns a report across all active agents.
     */
    @GetMapping("/analyze")
    public ResponseEntity<?> analyze(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Long agentId) {

        LocalDate localDate = date != null ? LocalDate.parse(date) : LocalDate.now();

        if (agentId != null) {
            List<FraudFlagDTO> flags = detectionService.analyzeAgent(agentId, localDate);
            return ResponseEntity.ok(Map.of(
                    "date", localDate.toString(),
                    "agentId", agentId,
                    "flagCount", flags.size(),
                    "flags", flags));
        }

        Map<Long, List<FraudFlagDTO>> report = detectionService.analyzeAllAgents(localDate);
        int totalFlags = report.values().stream().mapToInt(List::size).sum();
        return ResponseEntity.ok(Map.of(
                "date", localDate.toString(),
                "agentsFlagged", report.size(),
                "totalFlags", totalFlags,
                "report", report));
    }
}