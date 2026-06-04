package pl.edu.pb.smuggling.common.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pb.smuggling.common.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    
    @EntityGraph(attributePaths = {"changedBy"})
    Page<AuditLog> findAllByOrderByChangedAtDesc(Pageable pageable);
}
