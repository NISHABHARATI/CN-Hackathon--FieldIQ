package ai.creditnirvana.fieldiq.service;

import ai.creditnirvana.fieldiq.dto.AgentStatusDTO;
import ai.creditnirvana.fieldiq.dto.SupervisorDashboardDTO;
import ai.creditnirvana.fieldiq.entity.FieldVisit;
import ai.creditnirvana.fieldiq.entity.VisitOutcome;
import ai.creditnirvana.fieldiq.enums.OutcomeType;
import ai.creditnirvana.fieldiq.enums.VisitStatus;
import ai.creditnirvana.fieldiq.enums.VisitTrigger;
import ai.creditnirvana.fieldiq.repository.FieldVisitRepository;
import ai.creditnirvana.fieldiq.repository.VisitOutcomeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SupervisorService {

    private final FieldVisitRepository visitRepository;
    private final VisitOutcomeRepository outcomeRepository;
    private final AgentService agentService;

    public SupervisorService(FieldVisitRepository visitRepository,
                             VisitOutcomeRepository outcomeRepository,
                             AgentService agentService) {
        this.visitRepository = visitRepository;
        this.outcomeRepository = outcomeRepository;
        this.agentService = agentService;
    }

    @Transactional(readOnly = true)
    public SupervisorDashboardDTO getDashboard(LocalDate date) {
        List<FieldVisit> visits = visitRepository.findByScheduledDateOrderByAgentAscRouteOrderAsc(date);
        List<VisitOutcome> outcomes = outcomeRepository.findByVisitDate(date);

        long completed = visits.stream().filter(v -> v.getStatus() == VisitStatus.COMPLETED).count();
        long inProgress = visits.stream().filter(v -> v.getStatus() == VisitStatus.IN_PROGRESS).count();
        long pending = visits.stream().filter(v -> v.getStatus() == VisitStatus.PENDING).count();
        long skipped = visits.stream().filter(v -> v.getStatus() == VisitStatus.SKIPPED).count();

        BigDecimal totalCollected = outcomes.stream()
                .filter(o -> o.getAmountCollected() != null)
                .map(VisitOutcome::getAmountCollected)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long ptpCount = outcomes.stream()
                .filter(o -> o.getOutcome() == OutcomeType.PTP).count();
        long paidFull = outcomes.stream()
                .filter(o -> o.getOutcome() == OutcomeType.PAID_FULL).count();
        long paidPartial = outcomes.stream()
                .filter(o -> o.getOutcome() == OutcomeType.PAID_PARTIAL).count();

        // Trigger breakdown
        Map<String, Long> triggerBreakdown = new HashMap<>();
        for (VisitTrigger t : VisitTrigger.values()) {
            long count = visits.stream()
                    .filter(v -> v.getTriggers() != null && v.getTriggers().contains(t))
                    .count();
            triggerBreakdown.put(t.name(), count);
        }

        List<AgentStatusDTO> agentStatuses = agentService.getAllAgentStatuses(date);
        long activeAgents = agentStatuses.stream()
                .filter(a -> a.getTotalVisitsToday() > 0).count();

        return SupervisorDashboardDTO.builder()
                .date(date)
                .totalVisitsScheduled(visits.size())
                .visitsCompleted(completed)
                .visitsInProgress(inProgress)
                .visitsPending(pending)
                .visitsSkipped(skipped)
                .totalAmountCollected(totalCollected)
                .ptpCount(ptpCount)
                .paidFullCount(paidFull)
                .paidPartialCount(paidPartial)
                .activeAgents(activeAgents)
                .agentStatuses(agentStatuses)
                .visitsByTrigger(triggerBreakdown)
                .build();
    }
}