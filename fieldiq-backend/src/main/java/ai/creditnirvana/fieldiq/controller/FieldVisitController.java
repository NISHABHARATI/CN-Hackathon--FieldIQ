package ai.creditnirvana.fieldiq.controller;

import ai.creditnirvana.fieldiq.dto.FieldVisitDTO;
import ai.creditnirvana.fieldiq.dto.VisitOutcomeRequest;
import ai.creditnirvana.fieldiq.service.FieldVisitService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/visits")
public class FieldVisitController {

    private final FieldVisitService fieldVisitService;

    public FieldVisitController(FieldVisitService fieldVisitService) {
        this.fieldVisitService = fieldVisitService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FieldVisitDTO> getVisit(@PathVariable Long id) {
        return ResponseEntity.ok(fieldVisitService.getVisit(id));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<FieldVisitDTO> startVisit(@PathVariable Long id) {
        return ResponseEntity.ok(fieldVisitService.startVisit(id));
    }

    @PostMapping("/{id}/outcome")
    public ResponseEntity<FieldVisitDTO> logOutcome(
            @PathVariable Long id,
            @RequestBody @Valid VisitOutcomeRequest request) {
        return ResponseEntity.ok(fieldVisitService.logOutcome(id, request));
    }

    @PutMapping("/{id}/skip")
    public ResponseEntity<FieldVisitDTO> skipVisit(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(fieldVisitService.skipVisit(id, reason));
    }
}