package ai.creditnirvana.fieldiq.service;

import ai.creditnirvana.fieldiq.dto.PrioritisedAccountDTO;
import ai.creditnirvana.fieldiq.entity.Account;
import ai.creditnirvana.fieldiq.enums.VisitTrigger;
import ai.creditnirvana.fieldiq.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountPrioritisationService {

    private static final Logger log = LoggerFactory.getLogger(AccountPrioritisationService.class);

    private static final double WEIGHT_DPD = 0.20;
    private static final double WEIGHT_CHANNELS_EXHAUSTED = 0.25;
    private static final double WEIGHT_LEGAL_NOTICE = 0.30;
    private static final double WEIGHT_ADDRESS_VERIFICATION = 0.15;
    private static final double WEIGHT_HIGH_VALUE_STALLED = 0.10;

    private static final int DPD_THRESHOLD = 60;
    private static final int HIGH_VALUE_DPD_THRESHOLD = 45;
    private static final int DARK_DAYS_THRESHOLD = 30;
    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("500000");
    private static final int MIN_BROKEN_PTP = 2;

    private final AccountRepository accountRepository;

    public AccountPrioritisationService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<PrioritisedAccountDTO> getPrioritisedAccounts(String city) {
        List<Account> accounts = city != null && !city.isBlank()
                ? accountRepository.findByCity(city)
                : accountRepository.findAll();

        return accounts.stream()
                .map(this::score)
                .filter(dto -> dto.getPriorityScore() > 0 || dto.getAlwaysInclude())
                .sorted(Comparator.comparingDouble(PrioritisedAccountDTO::getPriorityScore).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PrioritisedAccountDTO score(Account account) {
        LocalDate cutoff = LocalDate.now().minusDays(DARK_DAYS_THRESHOLD);

        double dpdScore = 0;
        double channelsScore = 0;
        double legalScore = 0;
        double addressScore = 0;
        double highValueScore = 0;
        boolean alwaysInclude = false;

        List<VisitTrigger> triggers = new ArrayList<>();

        if (account.getDpd() != null && account.getDpd() >= DPD_THRESHOLD) {
            dpdScore = WEIGHT_DPD;
            triggers.add(VisitTrigger.DPD_THRESHOLD);
        }

        if (isAllChannelsExhausted(account, cutoff)) {
            channelsScore = WEIGHT_CHANNELS_EXHAUSTED;
            triggers.add(VisitTrigger.ALL_CHANNELS_EXHAUSTED);
        }

        if (Boolean.TRUE.equals(account.getHasPendingLegalNotice())) {
            legalScore = WEIGHT_LEGAL_NOTICE;
            triggers.add(VisitTrigger.LEGAL_NOTICE);
            alwaysInclude = true;
        }

        if (isDarkAndUnverified(account, cutoff)) {
            addressScore = WEIGHT_ADDRESS_VERIFICATION;
            triggers.add(VisitTrigger.SKIP_ADDRESS_VERIFICATION);
        }

        if (isHighValueStalled(account)) {
            highValueScore = WEIGHT_HIGH_VALUE_STALLED;
            triggers.add(VisitTrigger.HIGH_VALUE_STALLED);
            alwaysInclude = true;
        }

        double totalScore = dpdScore + channelsScore + legalScore + addressScore + highValueScore;

        return PrioritisedAccountDTO.builder()
                .id(account.getId())
                .loanId(account.getLoanId())
                .borrowerName(account.getBorrowerName())
                .borrowerPhone(account.getBorrowerPhone())
                .address(account.getAddress())
                .latitude(account.getLatitude())
                .longitude(account.getLongitude())
                .dpd(account.getDpd())
                .outstandingAmount(account.getOutstandingAmount())
                .lastContactDate(account.getLastContactDate())
                .addressVerified(account.getAddressVerified())
                .hasPendingLegalNotice(account.getHasPendingLegalNotice())
                .brokenPtpCount(account.getBrokenPtpCount())
                .zone(account.getZone())
                .city(account.getCity())
                .priorityScore(Math.round(totalScore * 1000.0) / 1000.0)
                .triggers(triggers)
                .alwaysInclude(alwaysInclude)
                .dpdScore(dpdScore)
                .channelsExhaustedScore(channelsScore)
                .legalNoticeScore(legalScore)
                .addressVerificationScore(addressScore)
                .highValueStalledScore(highValueScore)
                .build();
    }

    private boolean isAllChannelsExhausted(Account account, LocalDate cutoff) {
        return account.getLastContactDate() == null
                || account.getLastContactDate().isBefore(cutoff);
    }

    private boolean isDarkAndUnverified(Account account, LocalDate cutoff) {
        boolean isDark = account.getLastContactDate() == null
                || account.getLastContactDate().isBefore(cutoff);
        return isDark && Boolean.FALSE.equals(account.getAddressVerified());
    }

    private boolean isHighValueStalled(Account account) {
        return account.getOutstandingAmount() != null
                && account.getOutstandingAmount().compareTo(HIGH_VALUE_THRESHOLD) >= 0
                && account.getDpd() != null
                && account.getDpd() >= HIGH_VALUE_DPD_THRESHOLD
                && account.getBrokenPtpCount() != null
                && account.getBrokenPtpCount() >= MIN_BROKEN_PTP;
    }
}