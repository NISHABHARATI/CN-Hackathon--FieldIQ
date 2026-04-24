package ai.creditnirvana.fieldiq.config;

import ai.creditnirvana.fieldiq.entity.Account;
import ai.creditnirvana.fieldiq.entity.FieldAgent;
import ai.creditnirvana.fieldiq.repository.AccountRepository;
import ai.creditnirvana.fieldiq.repository.FieldAgentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final AccountRepository accountRepository;
    private final FieldAgentRepository agentRepository;

    public DataSeeder(AccountRepository accountRepository, FieldAgentRepository agentRepository) {
        this.accountRepository = accountRepository;
        this.agentRepository = agentRepository;
    }

    @Override
    public void run(String... args) {
        if (agentRepository.count() > 0) {
            log.info("Seed data already present, skipping.");
            return;
        }
        seedAgents();
        seedAccounts();
        log.info("Seed data loaded: {} agents, {} accounts",
                agentRepository.count(), accountRepository.count());
    }

    private void seedAgents() {
        List<FieldAgent> agents = List.of(

            FieldAgent.builder()
                .agentCode("AGT-001").name("Rajesh Kumar").phone("9820001111")
                .homeLatitude(18.9320).homeLongitude(72.8347)
                .currentLatitude(18.9320).currentLongitude(72.8347)
                .city("Mumbai").zone("South Mumbai")
                .maxVisitsPerDay(8).isActive(true)
                .lastLocationUpdate(LocalDateTime.now())
                .languages(List.of("Hindi", "Marathi"))
                .knownZones(List.of("South Mumbai", "Bandra West"))
                .gender("MALE").successfulVisits(45).totalAssignedVisits(60)
                .build(),

            FieldAgent.builder()
                .agentCode("AGT-002").name("Priya Sharma").phone("9820002222")
                .homeLatitude(19.0544).homeLongitude(72.8322)
                .currentLatitude(19.0544).currentLongitude(72.8322)
                .city("Mumbai").zone("Bandra West")
                .maxVisitsPerDay(8).isActive(true)
                .lastLocationUpdate(LocalDateTime.now())
                .languages(List.of("Hindi", "Marathi", "Gujarati"))
                .knownZones(List.of("Bandra West"))
                .gender("FEMALE").successfulVisits(38).totalAssignedVisits(50)
                .build(),

            FieldAgent.builder()
                .agentCode("AGT-003").name("Anil Patil").phone("9820003333")
                .homeLatitude(19.1197).homeLongitude(72.8468)
                .currentLatitude(19.1197).currentLongitude(72.8468)
                .city("Mumbai").zone("Andheri East")
                .maxVisitsPerDay(8).isActive(true)
                .lastLocationUpdate(LocalDateTime.now())
                .languages(List.of("Marathi", "Hindi", "Kannada"))
                .knownZones(List.of("Andheri East"))
                .gender("MALE").successfulVisits(52).totalAssignedVisits(65)
                .build(),

            FieldAgent.builder()
                .agentCode("AGT-004").name("Sunita Desai").phone("9820004444")
                .homeLatitude(19.2183).homeLongitude(72.9781)
                .currentLatitude(19.2183).currentLongitude(72.9781)
                .city("Mumbai").zone("Thane")
                .maxVisitsPerDay(8).isActive(true)
                .lastLocationUpdate(LocalDateTime.now())
                .languages(List.of("Marathi", "Hindi"))
                .knownZones(List.of("Thane"))
                .gender("FEMALE").successfulVisits(30).totalAssignedVisits(42)
                .build(),

            FieldAgent.builder()
                .agentCode("AGT-005").name("Mohammed Shaikh").phone("9820005555")
                .homeLatitude(19.0330).homeLongitude(73.0297)
                .currentLatitude(19.0330).currentLongitude(73.0297)
                .city("Mumbai").zone("Navi Mumbai")
                .maxVisitsPerDay(8).isActive(true)
                .lastLocationUpdate(LocalDateTime.now())
                .languages(List.of("Urdu", "Hindi", "Marathi"))
                .knownZones(List.of("Navi Mumbai"))
                .gender("MALE").successfulVisits(41).totalAssignedVisits(55)
                .build()
        );
        agentRepository.saveAll(agents);
    }

    private void seedAccounts() {
        LocalDate today       = LocalDate.now();
        LocalDate longAgo     = today.minusDays(45);
        LocalDate recentContact = today.minusDays(5);

        List<Account> accounts = List.of(

            // ── South Mumbai ─────────────────────────────────────────────────
            Account.builder()
                .loanId("LN-SM-001").borrowerName("Vikram Malhotra").borrowerPhone("9821100001")
                .address("14 Nariman Point, Mumbai 400021")
                .latitude(18.9256).longitude(72.8242)
                .dpd(95).outstandingAmount(new BigDecimal("1850000"))
                .lastContactDate(longAgo).addressVerified(true)
                .hasPendingLegalNotice(true).brokenPtpCount(3).lastPtpDate(today.minusDays(20))
                .city("Mumbai").zone("South Mumbai").accountCreatedAt(today.minusYears(2))
                .preferredLanguage("Hindi").borrowerGender("MALE").borrowerType("BUSINESS")
                .requiresGenderSensitivity(false).isHighRisk(true)
                .build(),

            Account.builder()
                .loanId("LN-SM-002").borrowerName("Deepa Iyer").borrowerPhone("9821100002")
                .address("7 Marine Drive, Churchgate, Mumbai 400020")
                .latitude(18.9441).longitude(72.8234)
                .dpd(72).outstandingAmount(new BigDecimal("420000"))
                .lastContactDate(longAgo).addressVerified(true)
                .hasPendingLegalNotice(false).brokenPtpCount(1).lastPtpDate(today.minusDays(40))
                .city("Mumbai").zone("South Mumbai").accountCreatedAt(today.minusYears(1))
                .preferredLanguage("Tamil").borrowerGender("FEMALE").borrowerType("SALARIED")
                .requiresGenderSensitivity(true).isHighRisk(false)
                .build(),

            Account.builder()
                .loanId("LN-SM-003").borrowerName("Suresh Nair").borrowerPhone("9821100003")
                .address("22 Colaba Causeway, Mumbai 400005")
                .latitude(18.9067).longitude(72.8147)
                .dpd(60).outstandingAmount(new BigDecimal("7200000"))
                .lastContactDate(recentContact).addressVerified(true)
                .hasPendingLegalNotice(false).brokenPtpCount(2).lastPtpDate(today.minusDays(15))
                .city("Mumbai").zone("South Mumbai").accountCreatedAt(today.minusYears(3))
                .preferredLanguage("Malayalam").borrowerGender("MALE").borrowerType("BUSINESS")
                .requiresGenderSensitivity(false).isHighRisk(false)
                .build(),

            Account.builder()
                .loanId("LN-SM-004").borrowerName("Kavita Menon").borrowerPhone("9821100004")
                .address("3 Wodehouse Road, Colaba, Mumbai 400005")
                .latitude(18.9150).longitude(72.8254)
                .dpd(38).outstandingAmount(new BigDecimal("280000"))
                .lastContactDate(longAgo).addressVerified(false)
                .hasPendingLegalNotice(false).brokenPtpCount(0)
                .city("Mumbai").zone("South Mumbai").accountCreatedAt(today.minusYears(1))
                .preferredLanguage("Malayalam").borrowerGender("FEMALE").borrowerType("SALARIED")
                .requiresGenderSensitivity(true).isHighRisk(false)
                .build(),

            // ── Bandra West ──────────────────────────────────────────────────
            Account.builder()
                .loanId("LN-BW-001").borrowerName("Arjun Kapoor").borrowerPhone("9821200001")
                .address("15 Turner Road, Bandra West, Mumbai 400050")
                .latitude(19.0596).longitude(72.8295)
                .dpd(85).outstandingAmount(new BigDecimal("960000"))
                .lastContactDate(longAgo).addressVerified(true)
                .hasPendingLegalNotice(true).brokenPtpCount(2).lastPtpDate(today.minusDays(30))
                .city("Mumbai").zone("Bandra West").accountCreatedAt(today.minusYears(2))
                .preferredLanguage("Hindi").borrowerGender("MALE").borrowerType("BUSINESS")
                .requiresGenderSensitivity(false).isHighRisk(true)
                .build(),

            Account.builder()
                .loanId("LN-BW-002").borrowerName("Sneha Gupta").borrowerPhone("9821200002")
                .address("28 Hill Road, Bandra West, Mumbai 400050")
                .latitude(19.0542).longitude(72.8356)
                .dpd(55).outstandingAmount(new BigDecimal("340000"))
                .lastContactDate(longAgo).addressVerified(true)
                .hasPendingLegalNotice(false).brokenPtpCount(1)
                .city("Mumbai").zone("Bandra West").accountCreatedAt(today.minusYears(1))
                .preferredLanguage("Hindi").borrowerGender("FEMALE").borrowerType("SALARIED")
                .requiresGenderSensitivity(false).isHighRisk(false)
                .build(),

            Account.builder()
                .loanId("LN-BW-003").borrowerName("Rahul Verma").borrowerPhone("9821200003")
                .address("5 Pali Hill, Bandra West, Mumbai 400050")
                .latitude(19.0691).longitude(72.8237)
                .dpd(65).outstandingAmount(new BigDecimal("190000"))
                .lastContactDate(today.minusDays(10)).addressVerified(true)
                .hasPendingLegalNotice(false).brokenPtpCount(0)
                .city("Mumbai").zone("Bandra West").accountCreatedAt(today.minusYears(1))
                .preferredLanguage("Hindi").borrowerGender("MALE").borrowerType("SELF_EMPLOYED")
                .requiresGenderSensitivity(false).isHighRisk(false)
                .build(),

            Account.builder()
                .loanId("LN-BW-004").borrowerName("Meena Choudhary").borrowerPhone("9821200004")
                .address("42 Carter Road, Bandra West, Mumbai 400050")
                .latitude(19.0758).longitude(72.8258)
                .dpd(90).outstandingAmount(new BigDecimal("12500000"))
                .lastContactDate(longAgo).addressVerified(true)
                .hasPendingLegalNotice(true).brokenPtpCount(4).lastPtpDate(today.minusDays(10))
                .city("Mumbai").zone("Bandra West").accountCreatedAt(today.minusYears(3))
                .preferredLanguage("Hindi").borrowerGender("FEMALE").borrowerType("BUSINESS")
                .requiresGenderSensitivity(true).isHighRisk(true)
                .build(),

            // ── Andheri East ─────────────────────────────────────────────────
            Account.builder()
                .loanId("LN-AE-001").borrowerName("Pratik Joshi").borrowerPhone("9821300001")
                .address("101 Andheri Kurla Road, Andheri East, Mumbai 400069")
                .latitude(19.1136).longitude(72.8697)
                .dpd(68).outstandingAmount(new BigDecimal("8900000"))
                .lastContactDate(today.minusDays(20)).addressVerified(true)
                .hasPendingLegalNotice(false).brokenPtpCount(3).lastPtpDate(today.minusDays(12))
                .city("Mumbai").zone("Andheri East").accountCreatedAt(today.minusYears(2))
                .preferredLanguage("Marathi").borrowerGender("MALE").borrowerType("SELF_EMPLOYED")
                .requiresGenderSensitivity(false).isHighRisk(false)
                .build(),

            Account.builder()
                .loanId("LN-AE-002").borrowerName("Asha Shetty").borrowerPhone("9821300002")
                .address("55 JB Nagar, Andheri East, Mumbai 400059")
                .latitude(19.1077).longitude(72.8622)
                .dpd(78).outstandingAmount(new BigDecimal("510000"))
                .lastContactDate(longAgo).addressVerified(true)
                .hasPendingLegalNotice(false).brokenPtpCount(2)
                .city("Mumbai").zone("Andheri East").accountCreatedAt(today.minusYears(1))
                .preferredLanguage("Kannada").borrowerGender("FEMALE").borrowerType("SALARIED")
                .requiresGenderSensitivity(true).isHighRisk(false)
                .build(),

            Account.builder()
                .loanId("LN-AE-003").borrowerName("Dinesh Rao").borrowerPhone("9821300003")
                .address("12 Chakala, Andheri East, Mumbai 400093")
                .latitude(19.1214).longitude(72.8533)
                .dpd(110).outstandingAmount(new BigDecimal("2200000"))
                .lastContactDate(longAgo).addressVerified(true)
                .hasPendingLegalNotice(true).brokenPtpCount(5).lastPtpDate(today.minusDays(25))
                .city("Mumbai").zone("Andheri East").accountCreatedAt(today.minusYears(4))
                .preferredLanguage("Kannada").borrowerGender("MALE").borrowerType("BUSINESS")
                .requiresGenderSensitivity(false).isHighRisk(true)
                .build(),

            Account.builder()
                .loanId("LN-AE-004").borrowerName("Pooja Singh").borrowerPhone("9821300004")
                .address("8 Sahar Road, Andheri East, Mumbai 400099")
                .latitude(19.1003).longitude(72.8735)
                .dpd(42).outstandingAmount(new BigDecimal("155000"))
                .lastContactDate(longAgo).addressVerified(false)
                .hasPendingLegalNotice(false).brokenPtpCount(1)
                .city("Mumbai").zone("Andheri East").accountCreatedAt(today.minusMonths(8))
                .preferredLanguage("Hindi").borrowerGender("FEMALE").borrowerType("SALARIED")
                .requiresGenderSensitivity(false).isHighRisk(false)
                .build(),

            Account.builder()
                .loanId("LN-AE-005").borrowerName("Ravi Kumar").borrowerPhone("9821300005")
                .address("23 MIDC, Andheri East, Mumbai 400093")
                .latitude(19.1168).longitude(72.8776)
                .dpd(63).outstandingAmount(new BigDecimal("320000"))
                .lastContactDate(today.minusDays(12)).addressVerified(true)
                .hasPendingLegalNotice(false).brokenPtpCount(0)
                .city("Mumbai").zone("Andheri East").accountCreatedAt(today.minusYears(1))
                .preferredLanguage("Hindi").borrowerGender("MALE").borrowerType("SELF_EMPLOYED")
                .requiresGenderSensitivity(false).isHighRisk(false)
                .build(),

            // ── Thane ────────────────────────────────────────────────────────
            Account.builder()
                .loanId("LN-TH-001").borrowerName("Geeta Pawar").borrowerPhone("9821400001")
                .address("14 Gokhale Road, Thane West, Thane 400602")
                .latitude(19.2030).longitude(72.9720)
                .dpd(88).outstandingAmount(new BigDecimal("1100000"))
                .lastContactDate(longAgo).addressVerified(true)
                .hasPendingLegalNotice(true).brokenPtpCount(3).lastPtpDate(today.minusDays(35))
                .city("Mumbai").zone("Thane").accountCreatedAt(today.minusYears(2))
                .preferredLanguage("Marathi").borrowerGender("FEMALE").borrowerType("SALARIED")
                .requiresGenderSensitivity(true).isHighRisk(true)
                .build(),

            Account.builder()
                .loanId("LN-TH-002").borrowerName("Harish Thakur").borrowerPhone("9821400002")
                .address("7 Naupada, Thane West, Thane 400602")
                .latitude(19.1979).longitude(72.9652)
                .dpd(55).outstandingAmount(new BigDecimal("6800000"))
                .lastContactDate(today.minusDays(18)).addressVerified(true)
                .hasPendingLegalNotice(false).brokenPtpCount(2).lastPtpDate(today.minusDays(8))
                .city("Mumbai").zone("Thane").accountCreatedAt(today.minusYears(3))
                .preferredLanguage("Marathi").borrowerGender("MALE").borrowerType("BUSINESS")
                .requiresGenderSensitivity(false).isHighRisk(false)
                .build(),

            Account.builder()
                .loanId("LN-TH-003").borrowerName("Lata More").borrowerPhone("9821400003")
                .address("33 Majiwada, Thane East, Thane 400601")
                .latitude(19.2285).longitude(72.9887)
                .dpd(71).outstandingAmount(new BigDecimal("270000"))
                .lastContactDate(longAgo).addressVerified(true)
                .hasPendingLegalNotice(false).brokenPtpCount(1)
                .city("Mumbai").zone("Thane").accountCreatedAt(today.minusYears(1))
                .preferredLanguage("Marathi").borrowerGender("FEMALE").borrowerType("SALARIED")
                .requiresGenderSensitivity(false).isHighRisk(false)
                .build(),

            Account.builder()
                .loanId("LN-TH-004").borrowerName("Santosh Shinde").borrowerPhone("9821400004")
                .address("19 Vartak Nagar, Thane West, Thane 400606")
                .latitude(19.2122).longitude(72.9560)
                .dpd(67).outstandingAmount(new BigDecimal("188000"))
                .lastContactDate(longAgo).addressVerified(false)
                .hasPendingLegalNotice(false).brokenPtpCount(0)
                .city("Mumbai").zone("Thane").accountCreatedAt(today.minusMonths(10))
                .preferredLanguage("Marathi").borrowerGender("MALE").borrowerType("SELF_EMPLOYED")
                .requiresGenderSensitivity(false).isHighRisk(false)
                .build(),

            // ── Navi Mumbai ──────────────────────────────────────────────────
            Account.builder()
                .loanId("LN-NM-001").borrowerName("Shilpa Kadam").borrowerPhone("9821500001")
                .address("Plot 5, Sector 11, Vashi, Navi Mumbai 400703")
                .latitude(19.0728).longitude(73.0027)
                .dpd(92).outstandingAmount(new BigDecimal("3400000"))
                .lastContactDate(longAgo).addressVerified(true)
                .hasPendingLegalNotice(true).brokenPtpCount(4).lastPtpDate(today.minusDays(22))
                .city("Mumbai").zone("Navi Mumbai").accountCreatedAt(today.minusYears(2))
                .preferredLanguage("Marathi").borrowerGender("FEMALE").borrowerType("BUSINESS")
                .requiresGenderSensitivity(true).isHighRisk(true)
                .build(),

            Account.builder()
                .loanId("LN-NM-002").borrowerName("Ganesh Kulkarni").borrowerPhone("9821500002")
                .address("12 Sector 17, Vashi, Navi Mumbai 400703")
                .latitude(19.0840).longitude(73.0080)
                .dpd(62).outstandingAmount(new BigDecimal("9100000"))
                .lastContactDate(today.minusDays(25)).addressVerified(true)
                .hasPendingLegalNotice(true).brokenPtpCount(2).lastPtpDate(today.minusDays(18))
                .city("Mumbai").zone("Navi Mumbai").accountCreatedAt(today.minusYears(3))
                .preferredLanguage("Marathi").borrowerGender("MALE").borrowerType("BUSINESS")
                .requiresGenderSensitivity(false).isHighRisk(true)
                .build(),

            Account.builder()
                .loanId("LN-NM-003").borrowerName("Rekha Pillai").borrowerPhone("9821500003")
                .address("8 Sector 9, Sanpada, Navi Mumbai 400705")
                .latitude(19.0630).longitude(73.0151)
                .dpd(61).outstandingAmount(new BigDecimal("430000"))
                .lastContactDate(today.minusDays(8)).addressVerified(true)
                .hasPendingLegalNotice(false).brokenPtpCount(1)
                .city("Mumbai").zone("Navi Mumbai").accountCreatedAt(today.minusYears(1))
                .preferredLanguage("Malayalam").borrowerGender("FEMALE").borrowerType("SALARIED")
                .requiresGenderSensitivity(false).isHighRisk(false)
                .build(),

            Account.builder()
                .loanId("LN-NM-004").borrowerName("Ajay Bhatt").borrowerPhone("9821500004")
                .address("45 Sector 2, Kharghar, Navi Mumbai 410210")
                .latitude(19.0474).longitude(73.0659)
                .dpd(49).outstandingAmount(new BigDecimal("215000"))
                .lastContactDate(longAgo).addressVerified(true)
                .hasPendingLegalNotice(false).brokenPtpCount(0)
                .city("Mumbai").zone("Navi Mumbai").accountCreatedAt(today.minusMonths(9))
                .preferredLanguage("Hindi").borrowerGender("MALE").borrowerType("SELF_EMPLOYED")
                .requiresGenderSensitivity(false).isHighRisk(false)
                .build(),

            Account.builder()
                .loanId("LN-NM-005").borrowerName("Kavitha Nair").borrowerPhone("9821500005")
                .address("22 Sector 21, Nerul, Navi Mumbai 400706")
                .latitude(19.0331).longitude(73.0187)
                .dpd(29).outstandingAmount(new BigDecimal("95000"))
                .lastContactDate(longAgo).addressVerified(false)
                .hasPendingLegalNotice(false).brokenPtpCount(0)
                .city("Mumbai").zone("Navi Mumbai").accountCreatedAt(today.minusMonths(6))
                .preferredLanguage("Malayalam").borrowerGender("FEMALE").borrowerType("SALARIED")
                .requiresGenderSensitivity(true).isHighRisk(false)
                .build()
        );

        accountRepository.saveAll(accounts);
    }
}