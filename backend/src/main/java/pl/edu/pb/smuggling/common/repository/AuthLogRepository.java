package pl.edu.pb.smuggling.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pb.smuggling.common.model.AuthLog;

import java.util.List;

@Repository
public interface AuthLogRepository extends JpaRepository<AuthLog, Integer> {
    List<AuthLog> findAllByOrderByAttemptedAtDesc();
}
