package pl.edu.pb.smuggling.cargo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pb.smuggling.cargo.model.Cargo;

import java.util.List;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Integer> {

    List<Cargo> findByTransportId(Integer transportId);

    @Query("""
            select c
            from Cargo c
            where c.transport is null
              and (c.order is null or c.order.id = :orderId)
            """)
    Page<Cargo> findAssignableToOrder(Integer orderId, Pageable pageable);

    @Query("select coalesce(sum(c.packagesCount), 0) from Cargo c where c.transport.id = :transportId")
    Integer sumPackagesByTransportId(Integer transportId);
}
