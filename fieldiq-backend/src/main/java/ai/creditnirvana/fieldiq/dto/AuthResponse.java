package ai.creditnirvana.fieldiq.dto;

public class AuthResponse {
    private String token;
    private Long agentId;
    private String name;
    private String role;
    private String zone;
    private String phone;

    public AuthResponse(String token, Long agentId, String name, String role, String zone, String phone) {
        this.token = token;
        this.agentId = agentId;
        this.name = name;
        this.role = role;
        this.zone = zone;
        this.phone = phone;
    }

    public String getToken() { return token; }
    public Long getAgentId() { return agentId; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getZone() { return zone; }
    public String getPhone() { return phone; }
}