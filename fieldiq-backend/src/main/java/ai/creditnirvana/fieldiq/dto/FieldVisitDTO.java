package ai.creditnirvana.fieldiq.dto;

import ai.creditnirvana.fieldiq.enums.VisitStatus;
import ai.creditnirvana.fieldiq.enums.VisitTrigger;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class FieldVisitDTO {
    private Long visitId;
    private LocalDate scheduledDate;
    private Integer routeOrder;
    private VisitStatus status;
    private Double priorityScore;
    private List<VisitTrigger> triggers;
    private Double distanceFromPrevious;
    private Double estimatedTravelMinutes;
    private Double agentMatchScore;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime visitWindowStart;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime visitWindowEnd;

    private Long accountId;
    private String loanId;
    private String borrowerName;
    private String borrowerPhone;
    private String address;
    private Double latitude;
    private Double longitude;
    private Integer dpd;
    private BigDecimal outstandingAmount;
    private Boolean hasPendingLegalNotice;
    private Integer brokenPtpCount;

    private Long agentId;
    private String agentName;

    private String outcomeType;
    private BigDecimal amountCollected;
    private LocalDate ptpDate;
    private String notes;
    private LocalDateTime completedAt;

    public Long getVisitId() { return visitId; }
    public void setVisitId(Long visitId) { this.visitId = visitId; }

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

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getLoanId() { return loanId; }
    public void setLoanId(String loanId) { this.loanId = loanId; }

    public String getBorrowerName() { return borrowerName; }
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }

    public String getBorrowerPhone() { return borrowerPhone; }
    public void setBorrowerPhone(String borrowerPhone) { this.borrowerPhone = borrowerPhone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getDpd() { return dpd; }
    public void setDpd(Integer dpd) { this.dpd = dpd; }

    public BigDecimal getOutstandingAmount() { return outstandingAmount; }
    public void setOutstandingAmount(BigDecimal outstandingAmount) { this.outstandingAmount = outstandingAmount; }

    public Boolean getHasPendingLegalNotice() { return hasPendingLegalNotice; }
    public void setHasPendingLegalNotice(Boolean hasPendingLegalNotice) { this.hasPendingLegalNotice = hasPendingLegalNotice; }

    public Integer getBrokenPtpCount() { return brokenPtpCount; }
    public void setBrokenPtpCount(Integer brokenPtpCount) { this.brokenPtpCount = brokenPtpCount; }

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }

    public String getOutcomeType() { return outcomeType; }
    public void setOutcomeType(String outcomeType) { this.outcomeType = outcomeType; }

    public BigDecimal getAmountCollected() { return amountCollected; }
    public void setAmountCollected(BigDecimal amountCollected) { this.amountCollected = amountCollected; }

    public LocalDate getPtpDate() { return ptpDate; }
    public void setPtpDate(LocalDate ptpDate) { this.ptpDate = ptpDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public static FieldVisitDTOBuilder builder() { return new FieldVisitDTOBuilder(); }

    public static class FieldVisitDTOBuilder {
        private Long visitId;
        private LocalDate scheduledDate;
        private Integer routeOrder;
        private VisitStatus status;
        private Double priorityScore;
        private List<VisitTrigger> triggers;
        private Double distanceFromPrevious;
        private Double estimatedTravelMinutes;
        private Double agentMatchScore;
        private LocalTime visitWindowStart;
        private LocalTime visitWindowEnd;
        private Long accountId;
        private String loanId;
        private String borrowerName;
        private String borrowerPhone;
        private String address;
        private Double latitude;
        private Double longitude;
        private Integer dpd;
        private BigDecimal outstandingAmount;
        private Boolean hasPendingLegalNotice;
        private Integer brokenPtpCount;
        private Long agentId;
        private String agentName;
        private String outcomeType;
        private BigDecimal amountCollected;
        private LocalDate ptpDate;
        private String notes;
        private LocalDateTime completedAt;

        public FieldVisitDTOBuilder visitId(Long v) { this.visitId = v; return this; }
        public FieldVisitDTOBuilder scheduledDate(LocalDate v) { this.scheduledDate = v; return this; }
        public FieldVisitDTOBuilder routeOrder(Integer v) { this.routeOrder = v; return this; }
        public FieldVisitDTOBuilder status(VisitStatus v) { this.status = v; return this; }
        public FieldVisitDTOBuilder priorityScore(Double v) { this.priorityScore = v; return this; }
        public FieldVisitDTOBuilder triggers(List<VisitTrigger> v) { this.triggers = v; return this; }
        public FieldVisitDTOBuilder distanceFromPrevious(Double v) { this.distanceFromPrevious = v; return this; }
        public FieldVisitDTOBuilder estimatedTravelMinutes(Double v) { this.estimatedTravelMinutes = v; return this; }
        public FieldVisitDTOBuilder agentMatchScore(Double v) { this.agentMatchScore = v; return this; }
        public FieldVisitDTOBuilder visitWindowStart(LocalTime v) { this.visitWindowStart = v; return this; }
        public FieldVisitDTOBuilder visitWindowEnd(LocalTime v) { this.visitWindowEnd = v; return this; }
        public FieldVisitDTOBuilder accountId(Long v) { this.accountId = v; return this; }
        public FieldVisitDTOBuilder loanId(String v) { this.loanId = v; return this; }
        public FieldVisitDTOBuilder borrowerName(String v) { this.borrowerName = v; return this; }
        public FieldVisitDTOBuilder borrowerPhone(String v) { this.borrowerPhone = v; return this; }
        public FieldVisitDTOBuilder address(String v) { this.address = v; return this; }
        public FieldVisitDTOBuilder latitude(Double v) { this.latitude = v; return this; }
        public FieldVisitDTOBuilder longitude(Double v) { this.longitude = v; return this; }
        public FieldVisitDTOBuilder dpd(Integer v) { this.dpd = v; return this; }
        public FieldVisitDTOBuilder outstandingAmount(BigDecimal v) { this.outstandingAmount = v; return this; }
        public FieldVisitDTOBuilder hasPendingLegalNotice(Boolean v) { this.hasPendingLegalNotice = v; return this; }
        public FieldVisitDTOBuilder brokenPtpCount(Integer v) { this.brokenPtpCount = v; return this; }
        public FieldVisitDTOBuilder agentId(Long v) { this.agentId = v; return this; }
        public FieldVisitDTOBuilder agentName(String v) { this.agentName = v; return this; }
        public FieldVisitDTOBuilder outcomeType(String v) { this.outcomeType = v; return this; }
        public FieldVisitDTOBuilder amountCollected(BigDecimal v) { this.amountCollected = v; return this; }
        public FieldVisitDTOBuilder ptpDate(LocalDate v) { this.ptpDate = v; return this; }
        public FieldVisitDTOBuilder notes(String v) { this.notes = v; return this; }
        public FieldVisitDTOBuilder completedAt(LocalDateTime v) { this.completedAt = v; return this; }

        public FieldVisitDTO build() {
            FieldVisitDTO d = new FieldVisitDTO();
            d.visitId = visitId; d.scheduledDate = scheduledDate; d.routeOrder = routeOrder;
            d.status = status; d.priorityScore = priorityScore; d.triggers = triggers;
            d.distanceFromPrevious = distanceFromPrevious; d.estimatedTravelMinutes = estimatedTravelMinutes;
            d.agentMatchScore = agentMatchScore;
            d.visitWindowStart = visitWindowStart; d.visitWindowEnd = visitWindowEnd;
            d.accountId = accountId; d.loanId = loanId; d.borrowerName = borrowerName;
            d.borrowerPhone = borrowerPhone; d.address = address;
            d.latitude = latitude; d.longitude = longitude; d.dpd = dpd;
            d.outstandingAmount = outstandingAmount; d.hasPendingLegalNotice = hasPendingLegalNotice;
            d.brokenPtpCount = brokenPtpCount; d.agentId = agentId; d.agentName = agentName;
            d.outcomeType = outcomeType; d.amountCollected = amountCollected;
            d.ptpDate = ptpDate; d.notes = notes; d.completedAt = completedAt;
            return d;
        }
    }
}