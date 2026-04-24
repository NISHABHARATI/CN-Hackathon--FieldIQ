package ai.creditnirvana.fieldiq.controller;

import ai.creditnirvana.fieldiq.dto.AgentStatusDTO;
import ai.creditnirvana.fieldiq.dto.SupervisorDashboardDTO;
import ai.creditnirvana.fieldiq.service.AgentService;
import ai.creditnirvana.fieldiq.service.SupervisorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/supervisor")
public class SupervisorController {

    private final SupervisorService supervisorService;
    private final AgentService agentService;

    public SupervisorController(SupervisorService supervisorService, AgentService agentService) {
        this.supervisorService = supervisorService;
        this.agentService = agentService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<SupervisorDashboardDTO> getDashboard(
            @RequestParam(required = false) String date) {
        LocalDate localDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return ResponseEntity.ok(supervisorService.getDashboard(localDate));
    }

    @GetMapping("/agents/locations")
    public ResponseEntity<List<AgentStatusDTO>> getAgentLocations(
            @RequestParam(required = false) String date) {
        LocalDate localDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return ResponseEntity.ok(agentService.getAllAgentStatuses(localDate));
    }

    /**
     * Real-time live positions for the supervisor map dashboard.
     * Returns each agent's current GPS, staleness in seconds, and a tracking
     * status: ONLINE (< 5 min since last ping), STALE (5-30 min), OFFLINE (> 30 min).
     */
    @GetMapping("/agents/live")
    public ResponseEntity<List<Map<String, Object>>> getLivePositions() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        List<Map<String, Object>> positions = agentService.getAllAgentStatuses(today).stream()
                .map(s -> {
                    long staleSec = s.getLastLocationUpdate() != null
                            ? ChronoUnit.SECONDS.between(s.getLastLocationUpdate(), now)
                            : Long.MAX_VALUE;

                    String status;
                    if (staleSec < 300) status = "ONLINE";
                    else if (staleSec < 1800) status = "STALE";
                    else status = "OFFLINE";

                    Map<String, Object> pos = new LinkedHashMap<>();
                    pos.put("agentId", s.getAgentId());
                    pos.put("agentCode", s.getAgentCode());
                    pos.put("agentName", s.getName());
                    pos.put("zone", s.getZone());
                    pos.put("currentLatitude", s.getCurrentLatitude());
                    pos.put("currentLongitude", s.getCurrentLongitude());
                    pos.put("lastLocationUpdate", s.getLastLocationUpdate());
                    pos.put("locationStalenessSeconds", staleSec == Long.MAX_VALUE ? null : staleSec);
                    pos.put("trackingStatus", status);
                    pos.put("completedToday", s.getCompletedVisits());
                    pos.put("totalToday", s.getTotalVisitsToday());
                    if (s.getCurrentVisitId() != null) {
                        Map<String, Object> cv = new LinkedHashMap<>();
                        cv.put("visitId", s.getCurrentVisitId());
                        cv.put("accountName", s.getCurrentAccountName());
                        cv.put("address", s.getCurrentAccountAddress());
                        pos.put("currentVisit", cv);
                    }
                    return pos;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(positions);
    }
}