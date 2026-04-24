package ai.creditnirvana.fieldiq.repository;

import ai.creditnirvana.fieldiq.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByCity(String city);

    // DPD >= threshold
    List<Account> findByDpdGreaterThanEqual(int dpd);

    // Legal notice pending
    List<Account> findByHasPendingLegalNoticeTrue();

    // All channels exhausted: no contact in last N days (or never contacted)
    @Query("SELECT a FROM Account a WHERE a.lastContactDate IS NULL OR a.lastContactDate < :cutoffDate")
    List<Account> findAllChannelsExhausted(@Param("cutoffDate") LocalDate cutoffDate);

    // Dark + address unverified
    @Query("SELECT a FROM Account a WHERE (a.lastContactDate IS NULL OR a.lastContactDate < :cutoffDate) AND a.addressVerified = false")
    List<Account> findDarkAndUnverified(@Param("cutoffDate") LocalDate cutoffDate);

    // High value stalled
    @Query("SELECT a FROM Account a WHERE a.outstandingAmount >= :minAmount AND a.dpd >= :minDpd AND a.brokenPtpCount >= :minBrokenPtp")
    List<Account> findHighValueStalled(
        @Param("minAmount") java.math.BigDecimal minAmount,
        @Param("minDpd") int minDpd,
        @Param("minBrokenPtp") int minBrokenPtp
    );
}