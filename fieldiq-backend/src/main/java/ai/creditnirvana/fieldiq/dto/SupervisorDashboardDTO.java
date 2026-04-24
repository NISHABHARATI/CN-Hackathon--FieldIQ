package ai.creditnirvana.fieldiq.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class SupervisorDashboardDTO {
    private LocalDate date;

    private long totalVisitsScheduled;
    private long visitsCompleted;
    private long visitsInProgress;
    private long visitsPending;
    private long visitsSkipped;

    private BigDecimal totalAmountCollected;
    private long ptpCount;
    private long paidFullCount;
    private long paidPartialCount;

    private long activeAgents;
    private List<AgentStatusDTO> agentStatuses;

    private Map<String, Long> visitsByTrigger;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public long getTotalVisitsScheduled() { return totalVisitsScheduled; }
    public void setTotalVisitsScheduled(long totalVisitsScheduled) { this.totalVisitsScheduled = totalVisitsScheduled; }

    public long getVisitsCompleted() { return visitsCompleted; }
    public void setVisitsCompleted(long visitsCompleted) { this.visitsCompleted = visitsCompleted; }

    public long getVisitsInProgress() { return visitsInProgress; }
    public void setVisitsInProgress(long visitsInProgress) { this.visitsInProgress = visitsInProgress; }

    public long getVisitsPending() { return visitsPending; }
    public void setVisitsPending(long visitsPending) { this.visitsPending = visitsPending; }

    public long getVisitsSkipped() { return visitsSkipped; }
    public void setVisitsSkipped(long visitsSkipped) { this.visitsSkipped = visitsSkipped; }

    public BigDecimal getTotalAmountCollected() { return totalAmountCollected; }
    public void setTotalAmountCollected(BigDecimal totalAmountCollected) { this.totalAmountCollected = totalAmountCollected; }

    public long getPtpCount() { return ptpCount; }
    public void setPtpCount(long ptpCount) { this.ptpCount = ptpCount; }

    public long getPaidFullCount() { return paidFullCount; }
    public void setPaidFullCount(long paidFullCount) { this.paidFullCount = paidFullCount; }

    public long getPaidPartialCount() { return paidPartialCount; }
    public void setPaidPartialCount(long paidPartialCount) { this.paidPartialCount = paidPartialCount; }

    public long getActiveAgents() { return activeAgents; }
    public void setActiveAgents(long activeAgents) { this.activeAgents = activeAgents; }

    public List<AgentStatusDTO> getAgentStatuses() { return agentStatuses; }
    public void setAgentStatuses(List<AgentStatusDTO> agentStatuses) { this.agentStatuses = agentStatuses; }

    public Map<String, Long> getVisitsByTrigger() { return visitsByTrigger; }
    public void setVisitsByTrigger(Map<String, Long> visitsByTrigger) { this.visitsByTrigger = visitsByTrigger; }

    public static SupervisorDashboardDTOBuilder builder() { return new SupervisorDashboardDTOBuilder(); }

    public static class SupervisorDashboardDTOBuilder {
        private LocalDate date;
        private long totalVisitsScheduled;
        private long visitsCompleted;
        private long visitsInProgress;
        private long visitsPending;
        private long visitsSkipped;
        private BigDecimal totalAmountCollected;
        private long ptpCount;
        private long paidFullCount;
        private long paidPartialCount;
        private long activeAgents;
        private List<AgentStatusDTO> agentStatuses;
        private Map<String, Long> visitsByTrigger;

        public SupervisorDashboardDTOBuilder date(LocalDate date) { this.date = date; return this; }
        public SupervisorDashboardDTOBuilder totalVisitsScheduled(long v) { this.totalVisitsScheduled = v; return this; }
        public SupervisorDashboardDTOBuilder visitsCompleted(long v) { this.visitsCompleted = v; return this; }
        public SupervisorDashboardDTOBuilder visitsInProgress(long v) { this.visitsInProgress = v; return this; }
        public SupervisorDashboardDTOBuilder visitsPending(long v) { this.visitsPending = v; return this; }
        public SupervisorDashboardDTOBuilder visitsSkipped(long v) { this.visitsSkipped = v; return this; }
        public SupervisorDashboardDTOBuilder totalAmountCollected(BigDecimal v) { this.totalAmountCollected = v; return this; }
        public SupervisorDashboardDTOBuilder ptpCount(long v) { this.ptpCount = v; return this; }
        public SupervisorDashboardDTOBuilder paidFullCount(long v) { this.paidFullCount = v; return this; }
        public SupervisorDashboardDTOBuilder paidPartialCount(long v) { this.paidPartialCount = v; return this; }
        public SupervisorDashboardDTOBuilder activeAgents(long v) { this.activeAgents = v; return this; }
        public SupervisorDashboardDTOBuilder agentStatuses(List<AgentStatusDTO> v) { this.agentStatuses = v; return this; }
        public SupervisorDashboardDTOBuilder visitsByTrigger(Map<String, Long> v) { this.visitsByTrigger = v; return this; }

        public SupervisorDashboardDTO build() {
            SupervisorDashboardDTO d = new SupervisorDashboardDTO();
            d.date = date; d.totalVisitsScheduled = totalVisitsScheduled;
            d.visitsCompleted = visitsCompleted; d.visitsInProgress = visitsInProgress;
            d.visitsPending = visitsPending; d.visitsSkipped = visitsSkipped;
            d.totalAmountCollected = totalAmountCollected; d.ptpCount = ptpCount;
            d.paidFullCount = paidFullCount; d.paidPartialCount = paidPartialCount;
            d.activeAgents = activeAgents; d.agentStatuses = agentStatuses;
            d.visitsByTrigger = visitsByTrigger;
            return d;
        }
    }
}