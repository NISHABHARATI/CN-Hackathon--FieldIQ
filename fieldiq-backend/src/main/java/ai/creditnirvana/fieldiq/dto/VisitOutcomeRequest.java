package ai.creditnirvana.fieldiq.dto;

import ai.creditnirvana.fieldiq.enums.OutcomeType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class VisitOutcomeRequest {

    @NotNull
    private OutcomeType outcome;

    private BigDecimal amountCollected;
    private LocalDate ptpDate;
    private BigDecimal ptpAmount;
    private String notes;
    private String evidencePhotoUrl;

    private Double visitLatitude;
    private Double visitLongitude;

    public OutcomeType getOutcome() { return outcome; }
    public void setOutcome(OutcomeType outcome) { this.outcome = outcome; }

    public BigDecimal getAmountCollected() { return amountCollected; }
    public void setAmountCollected(BigDecimal amountCollected) { this.amountCollected = amountCollected; }

    public LocalDate getPtpDate() { return ptpDate; }
    public void setPtpDate(LocalDate ptpDate) { this.ptpDate = ptpDate; }

    public BigDecimal getPtpAmount() { return ptpAmount; }
    public void setPtpAmount(BigDecimal ptpAmount) { this.ptpAmount = ptpAmount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getEvidencePhotoUrl() { return evidencePhotoUrl; }
    public void setEvidencePhotoUrl(String evidencePhotoUrl) { this.evidencePhotoUrl = evidencePhotoUrl; }

    public Double getVisitLatitude() { return visitLatitude; }
    public void setVisitLatitude(Double visitLatitude) { this.visitLatitude = visitLatitude; }

    public Double getVisitLongitude() { return visitLongitude; }
    public void setVisitLongitude(Double visitLongitude) { this.visitLongitude = visitLongitude; }
}