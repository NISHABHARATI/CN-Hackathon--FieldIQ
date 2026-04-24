package ai.creditnirvana.fieldiq.dto;

import java.time.LocalDate;

public class RouteGenerationRequest {
    private LocalDate date;
    private String city;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}