package pl.edu.pb.smuggling.cargo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pb.smuggling.cargo.model.WarehouseStock;

@Repository
public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, Integer> {
}
