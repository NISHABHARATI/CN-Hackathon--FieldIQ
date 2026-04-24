package ai.creditnirvana.fieldiq.repository;

import ai.creditnirvana.fieldiq.entity.FieldAgent;
import ai.creditnirvana.fieldiq.entity.FieldVisit;
import ai.creditnirvana.fieldiq.enums.VisitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface FieldVisitRepository extends JpaRepository<FieldVisit, Long> {

    List<FieldVisit> findByAgentAndScheduledDateOrderByRouteOrderAsc(FieldAgent agent, LocalDate date);

    List<FieldVisit> findByScheduledDateOrderByAgentAscRouteOrderAsc(LocalDate date);

    List<FieldVisit> findByScheduledDateAndStatus(LocalDate date, VisitStatus status);

    boolean existsByAccountIdAndScheduledDate(Long accountId, LocalDate date);

    @Query("SELECT SUM(vo.amountCollected) FROM VisitOutcome vo WHERE vo.visit.scheduledDate = :date")
    java.math.BigDecimal sumCollectedByDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(v) FROM FieldVisit v WHERE v.scheduledDate = :date AND v.status = :status")
    long countByDateAndStatus(@Param("date") LocalDate date, @Param("status") VisitStatus status);

    @Query("SELECT COUNT(v) FROM FieldVisit v WHERE v.scheduledDate = :date")
    long countByDate(@Param("date") LocalDate date);

    List<FieldVisit> findByAgentIdAndScheduledDate(Long agentId, LocalDate date);

    List<FieldVisit> findByAgentIdAndScheduledDateAndStatus(Long agentId, LocalDate date, VisitStatus status);
}