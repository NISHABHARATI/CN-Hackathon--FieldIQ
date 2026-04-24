package ai.creditnirvana.fieldiq.entity;

import ai.creditnirvana.fieldiq.enums.VisitStatus;
import ai.creditnirvana.fieldiq.enums.VisitTrigger;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "field_visits")
public class FieldVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agent_id")
    private FieldAgent agent;

    private LocalDate scheduledDate;
    private Integer routeOrder;

    @Enumerated(EnumType.STRING)
    private VisitStatus status = VisitStatus.PENDING;

    private Double priorityScore;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "visit_triggers", joinColumns = @JoinColumn(name = "visit_id"))
    @Enumerated(EnumType.STRING)
    private List<VisitTrigger> triggers;

    private Double distanceFromPrevious;
    private Double estimatedTravelMinutes;
    private Double agentMatchScore;

    /** Set when a borrower requests a specific time window during mid-route deferral. */
    private LocalTime visitWindowStart;
    private LocalTime visitWindowEnd;

    @OneToOne(mappedBy = "visit", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private VisitOutcome outcome;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FieldVisit() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public FieldAgent getAgent() { return agent; }
    public void setAgent(FieldAgent agent) { this.agent = agent; }

    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }

    public Integer getRouteOrder() { return routeOrder; }
    public void setRouteOrder(Integer routeOrder) { this.routeOrder = routeOrder; }

    public VisitStatus getStatus() { return status; }
    public void setStatus(VisitStatus status) { this.status = status; }

    public Double getPriorityScore() { return priorityScore; }
    public void setPriorityScore(Double priorityScore) { this.priorityScore = priorityScore; }

    public List<VisitTrigger> getTriggers() { return triggers; }
    public void setTriggers(List<VisitTrigger> triggers) { this.triggers = triggers; }

    public Double getDistanceFromPrevious() { return distanceFromPrevious; }
    public void setDistanceFromPrevious(Double distanceFromPrevious) { this.distanceFromPrevious = distanceFromPrevious; }

    public Double getEstimatedTravelMinutes() { return estimatedTravelMinutes; }
    public void setEstimatedTravelMinutes(Double estimatedTravelMinutes) { this.estimatedTravelMinutes = estimatedTravelMinutes; }

    public Double getAgentMatchScore() { return agentMatchScore; }
    public void setAgentMatchScore(Double agentMatchScore) { this.agentMatchScore = agentMatchScore; }

    public LocalTime getVisitWindowStart() { return visitWindowStart; }
    public void setVisitWindowStart(LocalTime visitWindowStart) { this.visitWindowStart = visitWindowStart; }

    public LocalTime getVisitWindowEnd() { return visitWindowEnd; }
    public void setVisitWindowEnd(LocalTime visitWindowEnd) { this.visitWindowEnd = visitWindowEnd; }

    public VisitOutcome getOutcome() { return outcome; }
    public void setOutcome(VisitOutcome outcome) { this.outcome = outcome; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static FieldVisitBuilder builder() { return new FieldVisitBuilder(); }

    public static class FieldVisitBuilder {
        private Long id;
        private Account account;
        private FieldAgent agent;
        private LocalDate scheduledDate;
        private Integer routeOrder;
        private VisitStatus status = VisitStatus.PENDING;
        private Double priorityScore;
        private List<VisitTrigger> triggers;
        private Double distanceFromPrevious;
        private Double estimatedTravelMinutes;
        private Double agentMatchScore;
        private LocalTime visitWindowStart;
        private LocalTime visitWindowEnd;
        private VisitOutcome outcome;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public FieldVisitBuilder id(Long id) { this.id = id; return this; }
        public FieldVisitBuilder account(Account account) { this.account = account; return this; }
        public FieldVisitBuilder agent(FieldAgent agent) { this.agent = agent; return this; }
        public FieldVisitBuilder scheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; return this; }
        public FieldVisitBuilder routeOrder(Integer routeOrder) { this.routeOrder = routeOrder; return this; }
        public FieldVisitBuilder status(VisitStatus status) { this.status = status; return this; }
        public FieldVisitBuilder priorityScore(Double priorityScore) { this.priorityScore = priorityScore; return this; }
        public FieldVisitBuilder triggers(List<VisitTrigger> triggers) { this.triggers = triggers; return this; }
        public FieldVisitBuilder distanceFromPrevious(Double distanceFromPrevious) { this.distanceFromPrevious = distanceFromPrevious; return this; }
        public FieldVisitBuilder estimatedTravelMinutes(Double estimatedTravelMinutes) { this.estimatedTravelMinutes = estimatedTravelMinutes; return this; }
        public FieldVisitBuilder agentMatchScore(Double agentMatchScore) { this.agentMatchScore = agentMatchScore; return this; }
        public FieldVisitBuilder visitWindowStart(LocalTime visitWindowStart) { this.visitWindowStart = visitWindowStart; return this; }
        public FieldVisitBuilder visitWindowEnd(LocalTime visitWindowEnd) { this.visitWindowEnd = visitWindowEnd; return this; }
        public FieldVisitBuilder outcome(VisitOutcome outcome) { this.outcome = outcome; return this; }
        public FieldVisitBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public FieldVisitBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public FieldVisit build() {
            FieldVisit v = new FieldVisit();
            v.id = id; v.account = account; v.agent = agent;
            v.scheduledDate = scheduledDate; v.routeOrder = routeOrder;
            v.status = status; v.priorityScore = priorityScore; v.triggers = triggers;
            v.distanceFromPrevious = distanceFromPrevious; v.estimatedTravelMinutes = estimatedTravelMinutes;
            v.agentMatchScore = agentMatchScore;
            v.visitWindowStart = visitWindowStart; v.visitWindowEnd = visitWindowEnd;
            v.outcome = outcome; v.createdAt = createdAt; v.updatedAt = updatedAt;
            return v;
        }
    }
}