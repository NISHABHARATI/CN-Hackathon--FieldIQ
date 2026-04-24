package ai.creditnirvana.fieldiq.service;

import ai.creditnirvana.fieldiq.dto.FieldVisitDTO;
import ai.creditnirvana.fieldiq.dto.VisitOutcomeRequest;
import ai.creditnirvana.fieldiq.entity.*;
import ai.creditnirvana.fieldiq.enums.OutcomeType;
import ai.creditnirvana.fieldiq.enums.VisitStatus;
import ai.creditnirvana.fieldiq.repository.AccountRepository;
import ai.creditnirvana.fieldiq.repository.FieldAgentRepository;
import ai.creditnirvana.fieldiq.repository.FieldVisitRepository;
import ai.creditnirvana.fieldiq.repository.VisitOutcomeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FieldVisitService {

    private final FieldVisitRepository visitRepository;
    private final FieldAgentRepository agentRepository;
    private final VisitOutcomeRepository outcomeRepository;
    private final AccountRepository accountRepository;

    public FieldVisitService(FieldVisitRepository visitRepository,
                             FieldAgentRepository agentRepository,
                             VisitOutcomeRepository outcomeRepository,
                             AccountRepository accountRepository) {
        this.visitRepository = visitRepository;
        this.agentRepository = agentRepository;
        this.outcomeRepository = outcomeRepository;
        this.accountRepository = accountRepository;
    }

    private static final Set<OutcomeType> CONTACT_OUTCOMES = Set.of(
            OutcomeType.PAID_FULL, OutcomeType.PAID_PARTIAL, OutcomeType.PTP,
            OutcomeType.REFUSED, OutcomeType.ALREADY_SETTLED,
            OutcomeType.CONTACT_LATER, OutcomeType.LEGAL_NOTICE_DELIVERED
    );

    @Transactional(readOnly = true)
    public List<FieldVisitDTO> getVisitsForAgent(Long agentId, LocalDate date) {
        FieldAgent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agent not found"));
        return visitRepository.findByAgentAndScheduledDateOrderByRouteOrderAsc(agent, date)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FieldVisitDTO> getAllVisitsForDate(LocalDate date) {
        return visitRepository.findByScheduledDateOrderByAgentAscRouteOrderAsc(date)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FieldVisitDTO getVisit(Long visitId) {
        return toDTO(findVisit(visitId));
    }

    @Transactional
    public FieldVisitDTO startVisit(Long visitId) {
        FieldVisit visit = findVisit(visitId);
        if (visit.getStatus() != VisitStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Visit is already " + visit.getStatus());
        }
        visit.setStatus(VisitStatus.IN_PROGRESS);
        visit.setUpdatedAt(LocalDateTime.now());
        return toDTO(visitRepository.save(visit));
    }

    @Transactional
    public FieldVisitDTO logOutcome(Long visitId, VisitOutcomeRequest request) {
        FieldVisit visit = findVisit(visitId);
        VisitStatus currentStatus = visit.getStatus();
        if (currentStatus == VisitStatus.COMPLETED
                || currentStatus == VisitStatus.SKIPPED
                || currentStatus == VisitStatus.RESCHEDULED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot log outcome for visit with status: " + currentStatus);
        }

        VisitOutcome outcome = VisitOutcome.builder()
                .visit(visit)
                .outcome(request.getOutcome())
                .amountCollected(request.getAmountCollected())
                .ptpDate(request.getPtpDate())
                .ptpAmount(request.getPtpAmount())
                .notes(request.getNotes())
                .evidencePhotoUrl(request.getEvidencePhotoUrl())
                .visitLatitude(request.getVisitLatitude())
                .visitLongitude(request.getVisitLongitude())
                .visitedAt(LocalDateTime.now())
                .build();

        outcomeRepository.save(outcome);

        visit.setOutcome(outcome);
        visit.setStatus(VisitStatus.COMPLETED);
        visit.setUpdatedAt(LocalDateTime.now());
        visitRepository.save(visit);

        Account account = visit.getAccount();
        account.setLastVisitDate(LocalDate.now());
        if (CONTACT_OUTCOMES.contains(request.getOutcome())) {
            account.setLastContactDate(LocalDate.now());
        }
        accountRepository.save(account);

        return toDTO(visit);
    }

    @Transactional
    public FieldVisitDTO skipVisit(Long visitId, String reason) {
        FieldVisit visit = findVisit(visitId);
        visit.setStatus(VisitStatus.SKIPPED);
        visit.setUpdatedAt(LocalDateTime.now());
        return toDTO(visitRepository.save(visit));
    }

    private FieldVisit findVisit(Long visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visit not found: " + visitId));
    }

    public FieldVisitDTO toDTO(FieldVisit v) {
        Account a = v.getAccount();
        FieldVisitDTO.FieldVisitDTOBuilder b = FieldVisitDTO.builder()
                .visitId(v.getId())
                .scheduledDate(v.getScheduledDate())
                .routeOrder(v.getRouteOrder())
                .status(v.getStatus())
                .priorityScore(v.getPriorityScore())
                .triggers(v.getTriggers())
                .distanceFromPrevious(v.getDistanceFromPrevious())
                .estimatedTravelMinutes(v.getEstimatedTravelMinutes())
                .agentMatchScore(v.getAgentMatchScore())
                .visitWindowStart(v.getVisitWindowStart())
                .visitWindowEnd(v.getVisitWindowEnd())
                .accountId(a.getId())
                .loanId(a.getLoanId())
                .borrowerName(a.getBorrowerName())
                .borrowerPhone(a.getBorrowerPhone())
                .address(a.getAddress())
                .latitude(a.getLatitude())
                .longitude(a.getLongitude())
                .dpd(a.getDpd())
                .outstandingAmount(a.getOutstandingAmount())
                .hasPendingLegalNotice(a.getHasPendingLegalNotice())
                .brokenPtpCount(a.getBrokenPtpCount())
                .agentId(v.getAgent().getId())
                .agentName(v.getAgent().getName());

        if (v.getOutcome() != null) {
            VisitOutcome o = v.getOutcome();
            b.outcomeType(o.getOutcome() != null ? o.getOutcome().name() : null)
             .amountCollected(o.getAmountCollected())
             .ptpDate(o.getPtpDate())
             .notes(o.getNotes())
             .completedAt(o.getVisitedAt());
        }

        return b.build();
    }
}