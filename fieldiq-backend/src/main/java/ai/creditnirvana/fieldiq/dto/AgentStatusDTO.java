package ai.creditnirvana.fieldiq.dto;

import java.time.LocalDateTime;

public class AgentStatusDTO {
    private Long agentId;
    private String agentCode;
    private String name;
    private String phone;
    private Double currentLatitude;
    private Double currentLongitude;
    private LocalDateTime lastLocationUpdate;
    private String zone;

    private int totalVisitsToday;
    private int completedVisits;
    private int pendingVisits;

    private Long currentVisitId;
    private String currentAccountName;
    private String currentAccountAddress;

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public String getAgentCode() { return agentCode; }
    public void setAgentCode(String agentCode) { this.agentCode = agentCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Double getCurrentLatitude() { return currentLatitude; }
    public void setCurrentLatitude(Double currentLatitude) { this.currentLatitude = currentLatitude; }

    public Double getCurrentLongitude() { return currentLongitude; }
    public void setCurrentLongitude(Double currentLongitude) { this.currentLongitude = currentLongitude; }

    public LocalDateTime getLastLocationUpdate() { return lastLocationUpdate; }
    public void setLastLocationUpdate(LocalDateTime lastLocationUpdate) { this.lastLocationUpdate = lastLocationUpdate; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public int getTotalVisitsToday() { return totalVisitsToday; }
    public void setTotalVisitsToday(int totalVisitsToday) { this.totalVisitsToday = totalVisitsToday; }

    public int getCompletedVisits() { return completedVisits; }
    public void setCompletedVisits(int completedVisits) { this.completedVisits = completedVisits; }

    public int getPendingVisits() { return pendingVisits; }
    public void setPendingVisits(int pendingVisits) { this.pendingVisits = pendingVisits; }

    public Long getCurrentVisitId() { return currentVisitId; }
    public void setCurrentVisitId(Long currentVisitId) { this.currentVisitId = currentVisitId; }

    public String getCurrentAccountName() { return currentAccountName; }
    public void setCurrentAccountName(String currentAccountName) { this.currentAccountName = currentAccountName; }

    public String getCurrentAccountAddress() { return currentAccountAddress; }
    public void setCurrentAccountAddress(String currentAccountAddress) { this.currentAccountAddress = currentAccountAddress; }

    public static AgentStatusDTOBuilder builder() { return new AgentStatusDTOBuilder(); }

    public static class AgentStatusDTOBuilder {
        private Long agentId;
        private String agentCode;
        private String name;
        private String phone;
        private Double currentLatitude;
        private Double currentLongitude;
        private LocalDateTime lastLocationUpdate;
        private String zone;
        private int totalVisitsToday;
        private int completedVisits;
        private int pendingVisits;
        private Long currentVisitId;
        private String currentAccountName;
        private String currentAccountAddress;

        public AgentStatusDTOBuilder agentId(Long agentId) { this.agentId = agentId; return this; }
        public AgentStatusDTOBuilder agentCode(String agentCode) { this.agentCode = agentCode; return this; }
        public AgentStatusDTOBuilder name(String name) { this.name = name; return this; }
        public AgentStatusDTOBuilder phone(String phone) { this.phone = phone; return this; }
        public AgentStatusDTOBuilder currentLatitude(Double currentLatitude) { this.currentLatitude = currentLatitude; return this; }
        public AgentStatusDTOBuilder currentLongitude(Double currentLongitude) { this.currentLongitude = currentLongitude; return this; }
        public AgentStatusDTOBuilder lastLocationUpdate(LocalDateTime lastLocationUpdate) { this.lastLocationUpdate = lastLocationUpdate; return this; }
        public AgentStatusDTOBuilder zone(String zone) { this.zone = zone; return this; }
        public AgentStatusDTOBuilder totalVisitsToday(int totalVisitsToday) { this.totalVisitsToday = totalVisitsToday; return this; }
        public AgentStatusDTOBuilder completedVisits(int completedVisits) { this.completedVisits = completedVisits; return this; }
        public AgentStatusDTOBuilder pendingVisits(int pendingVisits) { this.pendingVisits = pendingVisits; return this; }
        public AgentStatusDTOBuilder currentVisitId(Long currentVisitId) { this.currentVisitId = currentVisitId; return this; }
        public AgentStatusDTOBuilder currentAccountName(String currentAccountName) { this.currentAccountName = currentAccountName; return this; }
        public AgentStatusDTOBuilder currentAccountAddress(String currentAccountAddress) { this.currentAccountAddress = currentAccountAddress; return this; }

        public AgentStatusDTO build() {
            AgentStatusDTO d = new AgentStatusDTO();
            d.agentId = agentId; d.agentCode = agentCode; d.name = name; d.phone = phone;
            d.currentLatitude = currentLatitude; d.currentLongitude = currentLongitude;
            d.lastLocationUpdate = lastLocationUpdate; d.zone = zone;
            d.totalVisitsToday = totalVisitsToday; d.completedVisits = completedVisits;
            d.pendingVisits = pendingVisits; d.currentVisitId = currentVisitId;
            d.currentAccountName = currentAccountName; d.currentAccountAddress = currentAccountAddress;
            return d;
        }
    }
}