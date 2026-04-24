package ai.creditnirvana.fieldiq.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "field_agents")
public class FieldAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String agentCode;

    private String name;
    private String phone;

    private Double currentLatitude;
    private Double currentLongitude;

    private Double homeLatitude;
    private Double homeLongitude;

    private String city;
    private String zone;

    private Integer maxVisitsPerDay = 8;
    private Boolean isActive = true;

    private LocalDateTime lastLocationUpdate;

    // Agent-matching attributes (Layer 2)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "agent_languages", joinColumns = @JoinColumn(name = "agent_id"))
    @Column(name = "language")
    private List<String> languages = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "agent_known_zones", joinColumns = @JoinColumn(name = "agent_id"))
    @Column(name = "zone_name")
    private List<String> knownZones = new ArrayList<>();

    private String gender;
    private Integer successfulVisits = 0;
    private Integer totalAssignedVisits = 0;

    public FieldAgent() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Double getHomeLatitude() { return homeLatitude; }
    public void setHomeLatitude(Double homeLatitude) { this.homeLatitude = homeLatitude; }

    public Double getHomeLongitude() { return homeLongitude; }
    public void setHomeLongitude(Double homeLongitude) { this.homeLongitude = homeLongitude; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public Integer getMaxVisitsPerDay() { return maxVisitsPerDay; }
    public void setMaxVisitsPerDay(Integer maxVisitsPerDay) { this.maxVisitsPerDay = maxVisitsPerDay; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getLastLocationUpdate() { return lastLocationUpdate; }
    public void setLastLocationUpdate(LocalDateTime lastLocationUpdate) { this.lastLocationUpdate = lastLocationUpdate; }

    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }

    public List<String> getKnownZones() { return knownZones; }
    public void setKnownZones(List<String> knownZones) { this.knownZones = knownZones; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Integer getSuccessfulVisits() { return successfulVisits; }
    public void setSuccessfulVisits(Integer successfulVisits) { this.successfulVisits = successfulVisits; }

    public Integer getTotalAssignedVisits() { return totalAssignedVisits; }
    public void setTotalAssignedVisits(Integer totalAssignedVisits) { this.totalAssignedVisits = totalAssignedVisits; }

    public static FieldAgentBuilder builder() { return new FieldAgentBuilder(); }

    public static class FieldAgentBuilder {
        private Long id;
        private String agentCode;
        private String name;
        private String phone;
        private Double currentLatitude;
        private Double currentLongitude;
        private Double homeLatitude;
        private Double homeLongitude;
        private String city;
        private String zone;
        private Integer maxVisitsPerDay = 8;
        private Boolean isActive = true;
        private LocalDateTime lastLocationUpdate;
        private List<String> languages = new ArrayList<>();
        private List<String> knownZones = new ArrayList<>();
        private String gender;
        private Integer successfulVisits = 0;
        private Integer totalAssignedVisits = 0;

        public FieldAgentBuilder id(Long id) { this.id = id; return this; }
        public FieldAgentBuilder agentCode(String agentCode) { this.agentCode = agentCode; return this; }
        public FieldAgentBuilder name(String name) { this.name = name; return this; }
        public FieldAgentBuilder phone(String phone) { this.phone = phone; return this; }
        public FieldAgentBuilder currentLatitude(Double v) { this.currentLatitude = v; return this; }
        public FieldAgentBuilder currentLongitude(Double v) { this.currentLongitude = v; return this; }
        public FieldAgentBuilder homeLatitude(Double v) { this.homeLatitude = v; return this; }
        public FieldAgentBuilder homeLongitude(Double v) { this.homeLongitude = v; return this; }
        public FieldAgentBuilder city(String city) { this.city = city; return this; }
        public FieldAgentBuilder zone(String zone) { this.zone = zone; return this; }
        public FieldAgentBuilder maxVisitsPerDay(Integer v) { this.maxVisitsPerDay = v; return this; }
        public FieldAgentBuilder isActive(Boolean v) { this.isActive = v; return this; }
        public FieldAgentBuilder lastLocationUpdate(LocalDateTime v) { this.lastLocationUpdate = v; return this; }
        public FieldAgentBuilder languages(List<String> languages) { this.languages = languages; return this; }
        public FieldAgentBuilder knownZones(List<String> knownZones) { this.knownZones = knownZones; return this; }
        public FieldAgentBuilder gender(String gender) { this.gender = gender; return this; }
        public FieldAgentBuilder successfulVisits(Integer v) { this.successfulVisits = v; return this; }
        public FieldAgentBuilder totalAssignedVisits(Integer v) { this.totalAssignedVisits = v; return this; }

        public FieldAgent build() {
            FieldAgent a = new FieldAgent();
            a.id = id; a.agentCode = agentCode; a.name = name; a.phone = phone;
            a.currentLatitude = currentLatitude; a.currentLongitude = currentLongitude;
            a.homeLatitude = homeLatitude; a.homeLongitude = homeLongitude;
            a.city = city; a.zone = zone;
            a.maxVisitsPerDay = maxVisitsPerDay; a.isActive = isActive;
            a.lastLocationUpdate = lastLocationUpdate;
            a.languages = languages; a.knownZones = knownZones;
            a.gender = gender;
            a.successfulVisits = successfulVisits;
            a.totalAssignedVisits = totalAssignedVisits;
            return a;
        }
    }
}