package ai.creditnirvana.fieldiq.service;

import ai.creditnirvana.fieldiq.dto.AgentStatusDTO;
import ai.creditnirvana.fieldiq.entity.FieldAgent;
import ai.creditnirvana.fieldiq.entity.FieldVisit;
import ai.creditnirvana.fieldiq.enums.VisitStatus;
import ai.creditnirvana.fieldiq.repository.FieldAgentRepository;
import ai.creditnirvana.fieldiq.repository.FieldVisitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgentService {

    private final FieldAgentRepository agentRepository;
    private final FieldVisitRepository visitRepository;

    public AgentService(FieldAgentRepository agentRepository, FieldVisitRepository visitRepository) {
        this.agentRepository = agentRepository;
        this.visitRepository = visitRepository;
    }

    @Transactional
    public AgentStatusDTO updateLocation(Long agentId, Double latitude, Double longitude) {
        FieldAgent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agent not found"));
        agent.setCurrentLatitude(latitude);
        agent.setCurrentLongitude(longitude);
        agent.setLastLocationUpdate(LocalDateTime.now());
        agentRepository.save(agent);
        return buildStatus(agent, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<AgentStatusDTO> getAllAgentStatuses(LocalDate date) {
        return agentRepository.findByIsActiveTrue().stream()
                .map(a -> buildStatus(a, date))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AgentStatusDTO getAgentStatus(Long agentId, LocalDate date) {
        FieldAgent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agent not found"));
        return buildStatus(agent, date);
    }

    private AgentStatusDTO buildStatus(FieldAgent agent, LocalDate date) {
        List<FieldVisit> visits = visitRepository.findByAgentAndScheduledDateOrderByRouteOrderAsc(agent, date);

        long completed = visits.stream().filter(v -> v.getStatus() == VisitStatus.COMPLETED).count();
        long pending = visits.stream().filter(v -> v.getStatus() == VisitStatus.PENDING).count();

        FieldVisit inProgress = visits.stream()
                .filter(v -> v.getStatus() == VisitStatus.IN_PROGRESS)
                .findFirst().orElse(null);

        AgentStatusDTO.AgentStatusDTOBuilder b = AgentStatusDTO.builder()
                .agentId(agent.getId())
                .agentCode(agent.getAgentCode())
                .name(agent.getName())
                .phone(agent.getPhone())
                .currentLatitude(agent.getCurrentLatitude())
                .currentLongitude(agent.getCurrentLongitude())
                .lastLocationUpdate(agent.getLastLocationUpdate())
                .zone(agent.getZone())
                .totalVisitsToday(visits.size())
                .completedVisits((int) completed)
                .pendingVisits((int) pending);

        if (inProgress != null) {
            b.currentVisitId(inProgress.getId())
             .currentAccountName(inProgress.getAccount().getBorrowerName())
             .currentAccountAddress(inProgress.getAccount().getAddress());
        }

        return b.build();
    }
}