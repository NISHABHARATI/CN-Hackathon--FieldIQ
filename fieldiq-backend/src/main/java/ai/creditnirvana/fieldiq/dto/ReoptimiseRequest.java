package ai.creditnirvana.fieldiq.dto;

import java.time.LocalDate;

public class ReoptimiseRequest {

    private Long agentId;
    private LocalDate date;
    private Long deferVisitId;
    private Double currentLat;
    private Double currentLng;

    /**
     * Optional: borrower's requested time window (HH:mm).
     * If provided, the deferred visit is inserted at the position where
     * the agent is estimated to arrive within this window rather than at end-of-day.
     */
    private String visitWindowStart;  // e.g. "14:30"
    private String visitWindowEnd;    // e.g. "16:00"

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Long getDeferVisitId() { return deferVisitId; }
    public void setDeferVisitId(Long deferVisitId) { this.deferVisitId = deferVisitId; }

    public Double getCurrentLat() { return currentLat; }
    public void setCurrentLat(Double currentLat) { this.currentLat = currentLat; }

    public Double getCurrentLng() { return currentLng; }
    public void setCurrentLng(Double currentLng) { this.currentLng = currentLng; }

    public String getVisitWindowStart() { return visitWindowStart; }
    public void setVisitWindowStart(String visitWindowStart) { this.visitWindowStart = visitWindowStart; }

    public String getVisitWindowEnd() { return visitWindowEnd; }
    public void setVisitWindowEnd(String visitWindowEnd) { this.visitWindowEnd = visitWindowEnd; }
}
