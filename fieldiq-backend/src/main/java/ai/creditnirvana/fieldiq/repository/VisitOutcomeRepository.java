package ai.creditnirvana.fieldiq.repository;

import ai.creditnirvana.fieldiq.entity.VisitOutcome;
import ai.creditnirvana.fieldiq.enums.OutcomeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface VisitOutcomeRepository extends JpaRepository<VisitOutcome, Long> {

    @Query("SELECT vo FROM VisitOutcome vo WHERE vo.visit.scheduledDate = :date")
    List<VisitOutcome> findByVisitDate(@Param("date") LocalDate date);

    @Query("SELECT vo FROM VisitOutcome vo WHERE vo.visit.scheduledDate = :date AND vo.outcome = :outcome")
    List<VisitOutcome> findByVisitDateAndOutcome(@Param("date") LocalDate date, @Param("outcome") OutcomeType outcome);
}