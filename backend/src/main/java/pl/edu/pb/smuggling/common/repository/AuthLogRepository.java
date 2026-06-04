package pl.edu.pb.smuggling.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pb.smuggling.common.model.AuthLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface AuthLogRepository extends JpaRepository<AuthLog, Integer> {
    Page<AuthLog> findAllByOrderByAttemptedAtDesc(Pageable pageable);
}
