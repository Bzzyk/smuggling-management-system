package pl.edu.pb.smuggling.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pb.smuggling.order.model.SmugglingOrder;

public interface SmugglingOrderRepository extends JpaRepository<SmugglingOrder, Integer> {
}
