package ai.creditnirvana.fieldiq.controller;

import ai.creditnirvana.fieldiq.dto.AuthRequest;
import ai.creditnirvana.fieldiq.dto.AuthResponse;
import ai.creditnirvana.fieldiq.entity.FieldAgent;
import ai.creditnirvana.fieldiq.repository.FieldAgentRepository;
import ai.creditnirvana.fieldiq.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final FieldAgentRepository agentRepository;
    private final JwtUtil jwtUtil;

    public AuthController(FieldAgentRepository agentRepository, JwtUtil jwtUtil) {
        this.agentRepository = agentRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        Optional<FieldAgent> opt = agentRepository.findByPhone(req.getPhone());
        if (opt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid phone or PIN"));
        }
        FieldAgent agent = opt.get();
        if (!req.getPin().equals(agent.getPin())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid phone or PIN"));
        }
        String token = jwtUtil.generate(agent.getId(), agent.getName(), agent.getRole(),
                agent.getZone(), agent.getPhone());
        return ResponseEntity.ok(new AuthResponse(token, agent.getId(), agent.getName(),
                agent.getRole(), agent.getZone(), agent.getPhone()));
    }
}