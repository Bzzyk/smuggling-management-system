package pl.edu.pb.smuggling.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pb.smuggling.common.model.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
}
