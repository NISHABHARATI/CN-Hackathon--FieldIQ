package ai.creditnirvana.fieldiq.repository;

import ai.creditnirvana.fieldiq.entity.FieldAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FieldAgentRepository extends JpaRepository<FieldAgent, Long> {

    Optional<FieldAgent> findByPhone(String phone);

    List<FieldAgent> findByIsActiveTrue();

    List<FieldAgent> findByCityAndIsActiveTrue(String city);

    List<FieldAgent> findByZoneAndIsActiveTrue(String zone);
}