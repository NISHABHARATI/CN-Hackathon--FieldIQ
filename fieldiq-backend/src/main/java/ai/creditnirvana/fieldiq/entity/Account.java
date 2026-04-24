package ai.creditnirvana.fieldiq.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String loanId;

    private String borrowerName;
    private String borrowerPhone;

    @Column(length = 500)
    private String address;

    private Double latitude;
    private Double longitude;

    private Integer dpd;

    @Column(precision = 15, scale = 2)
    private BigDecimal outstandingAmount;

    private LocalDate lastContactDate;
    private Boolean addressVerified;
    private Boolean hasPendingLegalNotice;

    private Integer brokenPtpCount;
    private LocalDate lastPtpDate;
    private LocalDate lastVisitDate;

    private String city;
    private String zone;

    private LocalDate accountCreatedAt;

    // Agent-matching & constraint fields (Layer 2 / Layer 3)
    private String preferredLanguage;
    private Boolean requiresGenderSensitivity = false;
    private String borrowerGender;
    private String borrowerType;   // SALARIED, SELF_EMPLOYED, BUSINESS
    private Boolean isHighRisk = false;

    public Account() {}

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

    public LocalDate getLastPtpDate() { return lastPtpDate; }
    public void setLastPtpDate(LocalDate lastPtpDate) { this.lastPtpDate = lastPtpDate; }

    public LocalDate getLastVisitDate() { return lastVisitDate; }
    public void setLastVisitDate(LocalDate lastVisitDate) { this.lastVisitDate = lastVisitDate; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public LocalDate getAccountCreatedAt() { return accountCreatedAt; }
    public void setAccountCreatedAt(LocalDate accountCreatedAt) { this.accountCreatedAt = accountCreatedAt; }

    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }

    public Boolean getRequiresGenderSensitivity() { return requiresGenderSensitivity; }
    public void setRequiresGenderSensitivity(Boolean requiresGenderSensitivity) { this.requiresGenderSensitivity = requiresGenderSensitivity; }

    public String getBorrowerGender() { return borrowerGender; }
    public void setBorrowerGender(String borrowerGender) { this.borrowerGender = borrowerGender; }

    public String getBorrowerType() { return borrowerType; }
    public void setBorrowerType(String borrowerType) { this.borrowerType = borrowerType; }

    public Boolean getIsHighRisk() { return isHighRisk; }
    public void setIsHighRisk(Boolean isHighRisk) { this.isHighRisk = isHighRisk; }

    public static AccountBuilder builder() { return new AccountBuilder(); }

    public static class AccountBuilder {
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
        private LocalDate lastPtpDate;
        private LocalDate lastVisitDate;
        private String city;
        private String zone;
        private LocalDate accountCreatedAt;
        private String preferredLanguage;
        private Boolean requiresGenderSensitivity = false;
        private String borrowerGender;
        private String borrowerType;
        private Boolean isHighRisk = false;

        public AccountBuilder id(Long id) { this.id = id; return this; }
        public AccountBuilder loanId(String loanId) { this.loanId = loanId; return this; }
        public AccountBuilder borrowerName(String borrowerName) { this.borrowerName = borrowerName; return this; }
        public AccountBuilder borrowerPhone(String borrowerPhone) { this.borrowerPhone = borrowerPhone; return this; }
        public AccountBuilder address(String address) { this.address = address; return this; }
        public AccountBuilder latitude(Double latitude) { this.latitude = latitude; return this; }
        public AccountBuilder longitude(Double longitude) { this.longitude = longitude; return this; }
        public AccountBuilder dpd(Integer dpd) { this.dpd = dpd; return this; }
        public AccountBuilder outstandingAmount(BigDecimal outstandingAmount) { this.outstandingAmount = outstandingAmount; return this; }
        public AccountBuilder lastContactDate(LocalDate lastContactDate) { this.lastContactDate = lastContactDate; return this; }
        public AccountBuilder addressVerified(Boolean addressVerified) { this.addressVerified = addressVerified; return this; }
        public AccountBuilder hasPendingLegalNotice(Boolean hasPendingLegalNotice) { this.hasPendingLegalNotice = hasPendingLegalNotice; return this; }
        public AccountBuilder brokenPtpCount(Integer brokenPtpCount) { this.brokenPtpCount = brokenPtpCount; return this; }
        public AccountBuilder lastPtpDate(LocalDate lastPtpDate) { this.lastPtpDate = lastPtpDate; return this; }
        public AccountBuilder lastVisitDate(LocalDate lastVisitDate) { this.lastVisitDate = lastVisitDate; return this; }
        public AccountBuilder city(String city) { this.city = city; return this; }
        public AccountBuilder zone(String zone) { this.zone = zone; return this; }
        public AccountBuilder accountCreatedAt(LocalDate accountCreatedAt) { this.accountCreatedAt = accountCreatedAt; return this; }
        public AccountBuilder preferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; return this; }
        public AccountBuilder requiresGenderSensitivity(Boolean v) { this.requiresGenderSensitivity = v; return this; }
        public AccountBuilder borrowerGender(String borrowerGender) { this.borrowerGender = borrowerGender; return this; }
        public AccountBuilder borrowerType(String borrowerType) { this.borrowerType = borrowerType; return this; }
        public AccountBuilder isHighRisk(Boolean isHighRisk) { this.isHighRisk = isHighRisk; return this; }

        public Account build() {
            Account a = new Account();
            a.id = id; a.loanId = loanId; a.borrowerName = borrowerName;
            a.borrowerPhone = borrowerPhone; a.address = address;
            a.latitude = latitude; a.longitude = longitude; a.dpd = dpd;
            a.outstandingAmount = outstandingAmount; a.lastContactDate = lastContactDate;
            a.addressVerified = addressVerified; a.hasPendingLegalNotice = hasPendingLegalNotice;
            a.brokenPtpCount = brokenPtpCount; a.lastPtpDate = lastPtpDate;
            a.lastVisitDate = lastVisitDate; a.city = city; a.zone = zone;
            a.accountCreatedAt = accountCreatedAt;
            a.preferredLanguage = preferredLanguage;
            a.requiresGenderSensitivity = requiresGenderSensitivity;
            a.borrowerGender = borrowerGender;
            a.borrowerType = borrowerType;
            a.isHighRisk = isHighRisk;
            return a;
        }
    }
}