package pl.edu.pb.smuggling.cargo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pb.smuggling.cargo.model.WarehouseStock;

import java.util.Optional;

@Repository
public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, Integer> {
    Optional<WarehouseStock> findByCargoId(Integer cargoId);

    void deleteByCargoId(Integer cargoId);

    @Query("""
            select coalesce(sum(ws.quantity), 0)
            from WarehouseStock ws
            where ws.warehouse.id = :warehouseId
              and ws.cargo.id <> :cargoId
            """)
    Integer sumQuantityByWarehouseIdExcludingCargoId(Integer warehouseId, Integer cargoId);

    @Query("""
            select coalesce(sum(ws.quantity), 0)
            from WarehouseStock ws
            where ws.warehouse.id = :warehouseId
            """)
    Integer sumQuantityByWarehouseId(Integer warehouseId);
}
