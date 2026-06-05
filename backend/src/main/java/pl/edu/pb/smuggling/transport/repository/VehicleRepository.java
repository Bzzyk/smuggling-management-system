package pl.edu.pb.smuggling.transport.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pb.smuggling.transport.model.Vehicle;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);
    boolean existsByRegistrationNumber(String registrationNumber);

    @Query("""
            select v
            from Vehicle v
            where v.available = true
              and v.loadCapacity >= :requiredCapacity
            order by v.loadCapacity asc, v.registrationNumber asc
            """)
    Page<Vehicle> findAssignableVehicles(Integer requiredCapacity, Pageable pageable);
}
