package ai.creditnirvana.fieldiq.dto;

import java.time.LocalDateTime;

public class FraudFlagDTO {

    private String flagType;    // GPS_SPOOFING, DISPOSITION_STUFFING, DUPLICATE_VISIT, CLUSTER_FRAUD
    private String severity;    // HIGH, MEDIUM, LOW
    private Long agentId;
    private String agentName;
    private Long visitId;       // null for session-level flags (e.g. CLUSTER_FRAUD)
    private String details;
    private LocalDateTime detectedAt;

    public FraudFlagDTO() {}

    public FraudFlagDTO(String flagType, String severity, Long agentId, String agentName,
                        Long visitId, String details) {
        this.flagType = flagType;
        this.severity = severity;
        this.agentId = agentId;
        this.agentName = agentName;
        this.visitId = visitId;
        this.details = details;
        this.detectedAt = LocalDateTime.now();
    }

    public String getFlagType() { return flagType; }
    public void setFlagType(String flagType) { this.flagType = flagType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }

    public Long getVisitId() { return visitId; }
    public void setVisitId(Long visitId) { this.visitId = visitId; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
}