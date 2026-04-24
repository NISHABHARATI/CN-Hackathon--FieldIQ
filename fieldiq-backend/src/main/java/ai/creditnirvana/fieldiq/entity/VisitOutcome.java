package ai.creditnirvana.fieldiq.entity;

import ai.creditnirvana.fieldiq.enums.OutcomeType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "visit_outcomes")
public class VisitOutcome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "visit_id")
    private FieldVisit visit;

    @Enumerated(EnumType.STRING)
    private OutcomeType outcome;

    @Column(precision = 15, scale = 2)
    private BigDecimal amountCollected;

    private LocalDate ptpDate;

    @Column(precision = 15, scale = 2)
    private BigDecimal ptpAmount;

    @Column(length = 1000)
    private String notes;

    private String evidencePhotoUrl;

    private Double visitLatitude;
    private Double visitLongitude;

    private LocalDateTime visitedAt;

    public VisitOutcome() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public FieldVisit getVisit() { return visit; }
    public void setVisit(FieldVisit visit) { this.visit = visit; }

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

    public LocalDateTime getVisitedAt() { return visitedAt; }
    public void setVisitedAt(LocalDateTime visitedAt) { this.visitedAt = visitedAt; }

    public static VisitOutcomeBuilder builder() { return new VisitOutcomeBuilder(); }

    public static class VisitOutcomeBuilder {
        private Long id;
        private FieldVisit visit;
        private OutcomeType outcome;
        private BigDecimal amountCollected;
        private LocalDate ptpDate;
        private BigDecimal ptpAmount;
        private String notes;
        private String evidencePhotoUrl;
        private Double visitLatitude;
        private Double visitLongitude;
        private LocalDateTime visitedAt;

        public VisitOutcomeBuilder id(Long id) { this.id = id; return this; }
        public VisitOutcomeBuilder visit(FieldVisit visit) { this.visit = visit; return this; }
        public VisitOutcomeBuilder outcome(OutcomeType outcome) { this.outcome = outcome; return this; }
        public VisitOutcomeBuilder amountCollected(BigDecimal amountCollected) { this.amountCollected = amountCollected; return this; }
        public VisitOutcomeBuilder ptpDate(LocalDate ptpDate) { this.ptpDate = ptpDate; return this; }
        public VisitOutcomeBuilder ptpAmount(BigDecimal ptpAmount) { this.ptpAmount = ptpAmount; return this; }
        public VisitOutcomeBuilder notes(String notes) { this.notes = notes; return this; }
        public VisitOutcomeBuilder evidencePhotoUrl(String evidencePhotoUrl) { this.evidencePhotoUrl = evidencePhotoUrl; return this; }
        public VisitOutcomeBuilder visitLatitude(Double visitLatitude) { this.visitLatitude = visitLatitude; return this; }
        public VisitOutcomeBuilder visitLongitude(Double visitLongitude) { this.visitLongitude = visitLongitude; return this; }
        public VisitOutcomeBuilder visitedAt(LocalDateTime visitedAt) { this.visitedAt = visitedAt; return this; }

        public VisitOutcome build() {
            VisitOutcome o = new VisitOutcome();
            o.id = id; o.visit = visit; o.outcome = outcome;
            o.amountCollected = amountCollected; o.ptpDate = ptpDate; o.ptpAmount = ptpAmount;
            o.notes = notes; o.evidencePhotoUrl = evidencePhotoUrl;
            o.visitLatitude = visitLatitude; o.visitLongitude = visitLongitude;
            o.visitedAt = visitedAt;
            return o;
        }
    }
}