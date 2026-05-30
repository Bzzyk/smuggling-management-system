package pl.edu.pb.smuggling.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pb.smuggling.transport.model.TransportStatus;

import java.util.Optional;

public interface TransportStatusRepository extends JpaRepository<TransportStatus, Integer> {

    Optional<TransportStatus> findByName(String name);
}
