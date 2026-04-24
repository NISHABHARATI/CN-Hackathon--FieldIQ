package ai.creditnirvana.fieldiq.controller;

import ai.creditnirvana.fieldiq.dto.AgentLocationUpdate;
import ai.creditnirvana.fieldiq.dto.AgentStatusDTO;
import ai.creditnirvana.fieldiq.entity.FieldAgent;
import ai.creditnirvana.fieldiq.repository.FieldAgentRepository;
import ai.creditnirvana.fieldiq.service.AgentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final AgentService agentService;
    private final FieldAgentRepository agentRepository;

    public AgentController(AgentService agentService, FieldAgentRepository agentRepository) {
        this.agentService = agentService;
        this.agentRepository = agentRepository;
    }

    @GetMapping
    public ResponseEntity<List<FieldAgent>> getAllAgents() {
        return ResponseEntity.ok(agentRepository.findByIsActiveTrue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FieldAgent> getAgent(@PathVariable Long id) {
        return agentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agent not found: " + id));
    }

    @PutMapping("/{id}/location")
    public ResponseEntity<AgentStatusDTO> updateLocation(
            @PathVariable Long id,
            @RequestBody @Valid AgentLocationUpdate update) {
        return ResponseEntity.ok(agentService.updateLocation(id, update.getLatitude(), update.getLongitude()));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<AgentStatusDTO> getStatus(
            @PathVariable Long id,
            @RequestParam(required = false) String date) {
        LocalDate localDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return ResponseEntity.ok(agentService.getAgentStatus(id, localDate));
    }
}