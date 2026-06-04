package pl.edu.pb.smuggling.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pb.smuggling.transport.model.Transport;

public interface TransportRepository extends JpaRepository<Transport, Integer> {
    java.util.List<Transport> findByOrderId(Integer orderId);
}
