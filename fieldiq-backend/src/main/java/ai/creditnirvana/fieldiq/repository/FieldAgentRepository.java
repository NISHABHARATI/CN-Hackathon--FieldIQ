package ai.creditnirvana.fieldiq.repository;

import ai.creditnirvana.fieldiq.entity.FieldAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FieldAgentRepository extends JpaRepository<FieldAgent, Long> {

    List<FieldAgent> findByIsActiveTrue();

    List<FieldAgent> findByCityAndIsActiveTrue(String city);

    List<FieldAgent> findByZoneAndIsActiveTrue(String zone);
}