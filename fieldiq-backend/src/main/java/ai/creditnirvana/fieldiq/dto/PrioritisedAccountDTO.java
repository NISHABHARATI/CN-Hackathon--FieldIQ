package ai.creditnirvana.fieldiq.dto;

import ai.creditnirvana.fieldiq.enums.VisitTrigger;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PrioritisedAccountDTO {
    private Long id;
    private String loanId;
    private String borrowerName;
    private String borrowerPhone;
    private String address;
    private Double latitude;
    private Double longitude;
    private Integer dpd;
    private BigDecimal outstandingAmount;
    private LocalDate lastContactDate;
    private Boolean addressVerified;
    private Boolean hasPendingLegalNotice;
    private Integer brokenPtpCount;
    private String zone;
    private String city;

    private Double priorityScore;
    private List<VisitTrigger> triggers;
    private Boolean alwaysInclude;

    private Double dpdScore;
    private Double channelsExhaustedScore;
    private Double legalNoticeScore;
    private Double addressVerificationScore;
    private Double highValueStalledScore;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDate getLastContactDate() { return lastContactDate; }
    public void setLastContactDate(LocalDate lastContactDate) { this.lastContactDate = lastContactDate; }

    public Boolean getAddressVerified() { return addressVerified; }
    public void setAddressVerified(Boolean addressVerified) { this.addressVerified = addressVerified; }

    public Boolean getHasPendingLegalNotice() { return hasPendingLegalNotice; }
    public void setHasPendingLegalNotice(Boolean hasPendingLegalNotice) { this.hasPendingLegalNotice = hasPendingLegalNotice; }

    public Integer getBrokenPtpCount() { return brokenPtpCount; }
    public void setBrokenPtpCount(Integer brokenPtpCount) { this.brokenPtpCount = brokenPtpCount; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getPriorityScore() { return priorityScore; }
    public void setPriorityScore(Double priorityScore) { this.priorityScore = priorityScore; }

    public List<VisitTrigger> getTriggers() { return triggers; }
    public void setTriggers(List<VisitTrigger> triggers) { this.triggers = triggers; }

    public Boolean getAlwaysInclude() { return alwaysInclude; }
    public void setAlwaysInclude(Boolean alwaysInclude) { this.alwaysInclude = alwaysInclude; }

    public Double getDpdScore() { return dpdScore; }
    public void setDpdScore(Double dpdScore) { this.dpdScore = dpdScore; }

    public Double getChannelsExhaustedScore() { return channelsExhaustedScore; }
    public void setChannelsExhaustedScore(Double channelsExhaustedScore) { this.channelsExhaustedScore = channelsExhaustedScore; }

    public Double getLegalNoticeScore() { return legalNoticeScore; }
    public void setLegalNoticeScore(Double legalNoticeScore) { this.legalNoticeScore = legalNoticeScore; }

    public Double getAddressVerificationScore() { return addressVerificationScore; }
    public void setAddressVerificationScore(Double addressVerificationScore) { this.addressVerificationScore = addressVerificationScore; }

    public Double getHighValueStalledScore() { return highValueStalledScore; }
    public void setHighValueStalledScore(Double highValueStalledScore) { this.highValueStalledScore = highValueStalledScore; }

    public static PrioritisedAccountDTOBuilder builder() { return new PrioritisedAccountDTOBuilder(); }

    public static class PrioritisedAccountDTOBuilder {
        private Long id;
        private String loanId;
        private String borrowerName;
        private String borrowerPhone;
        private String address;
        private Double latitude;
        private Double longitude;
        private Integer dpd;
        private BigDecimal outstandingAmount;
        private LocalDate lastContactDate;
        private Boolean addressVerified;
        private Boolean hasPendingLegalNotice;
        private Integer brokenPtpCount;
        private String zone;
        private String city;
        private Double priorityScore;
        private List<VisitTrigger> triggers;
        private Boolean alwaysInclude;
        private Double dpdScore;
        private Double channelsExhaustedScore;
        private Double legalNoticeScore;
        private Double addressVerificationScore;
        private Double highValueStalledScore;

        public PrioritisedAccountDTOBuilder id(Long id) { this.id = id; return this; }
        public PrioritisedAccountDTOBuilder loanId(String loanId) { this.loanId = loanId; return this; }
        public PrioritisedAccountDTOBuilder borrowerName(String v) { this.borrowerName = v; return this; }
        public PrioritisedAccountDTOBuilder borrowerPhone(String v) { this.borrowerPhone = v; return this; }
        public PrioritisedAccountDTOBuilder address(String v) { this.address = v; return this; }
        public PrioritisedAccountDTOBuilder latitude(Double v) { this.latitude = v; return this; }
        public PrioritisedAccountDTOBuilder longitude(Double v) { this.longitude = v; return this; }
        public PrioritisedAccountDTOBuilder dpd(Integer v) { this.dpd = v; return this; }
        public PrioritisedAccountDTOBuilder outstandingAmount(BigDecimal v) { this.outstandingAmount = v; return this; }
        public PrioritisedAccountDTOBuilder lastContactDate(LocalDate v) { this.lastContactDate = v; return this; }
        public PrioritisedAccountDTOBuilder addressVerified(Boolean v) { this.addressVerified = v; return this; }
        public PrioritisedAccountDTOBuilder hasPendingLegalNotice(Boolean v) { this.hasPendingLegalNotice = v; return this; }
        public PrioritisedAccountDTOBuilder brokenPtpCount(Integer v) { this.brokenPtpCount = v; return this; }
        public PrioritisedAccountDTOBuilder zone(String v) { this.zone = v; return this; }
        public PrioritisedAccountDTOBuilder city(String v) { this.city = v; return this; }
        public PrioritisedAccountDTOBuilder priorityScore(Double v) { this.priorityScore = v; return this; }
        public PrioritisedAccountDTOBuilder triggers(List<VisitTrigger> v) { this.triggers = v; return this; }
        public PrioritisedAccountDTOBuilder alwaysInclude(Boolean v) { this.alwaysInclude = v; return this; }
        public PrioritisedAccountDTOBuilder dpdScore(Double v) { this.dpdScore = v; return this; }
        public PrioritisedAccountDTOBuilder channelsExhaustedScore(Double v) { this.channelsExhaustedScore = v; return this; }
        public PrioritisedAccountDTOBuilder legalNoticeScore(Double v) { this.legalNoticeScore = v; return this; }
        public PrioritisedAccountDTOBuilder addressVerificationScore(Double v) { this.addressVerificationScore = v; return this; }
        public PrioritisedAccountDTOBuilder highValueStalledScore(Double v) { this.highValueStalledScore = v; return this; }

        public PrioritisedAccountDTO build() {
            PrioritisedAccountDTO d = new PrioritisedAccountDTO();
            d.id = id; d.loanId = loanId; d.borrowerName = borrowerName;
            d.borrowerPhone = borrowerPhone; d.address = address;
            d.latitude = latitude; d.longitude = longitude; d.dpd = dpd;
            d.outstandingAmount = outstandingAmount; d.lastContactDate = lastContactDate;
            d.addressVerified = addressVerified; d.hasPendingLegalNotice = hasPendingLegalNotice;
            d.brokenPtpCount = brokenPtpCount; d.zone = zone; d.city = city;
            d.priorityScore = priorityScore; d.triggers = triggers; d.alwaysInclude = alwaysInclude;
            d.dpdScore = dpdScore; d.channelsExhaustedScore = channelsExhaustedScore;
            d.legalNoticeScore = legalNoticeScore; d.addressVerificationScore = addressVerificationScore;
            d.highValueStalledScore = highValueStalledScore;
            return d;
        }
    }
}