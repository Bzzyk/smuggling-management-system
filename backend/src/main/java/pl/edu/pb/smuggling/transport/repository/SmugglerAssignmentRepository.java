package pl.edu.pb.smuggling.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pb.smuggling.transport.model.SmugglerAssignment;

public interface SmugglerAssignmentRepository extends JpaRepository<SmugglerAssignment, Integer> {
    java.util.List<SmugglerAssignment> findByTransportId(Integer transportId);
}
