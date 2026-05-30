package pl.edu.pb.smuggling.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pb.smuggling.order.model.OrderStatus;

import java.util.Optional;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {

    Optional<OrderStatus> findByName(String name);
}
