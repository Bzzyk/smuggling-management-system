package pl.edu.pb.smuggling.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pb.smuggling.transport.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    boolean existsByRegistrationNumber(String registrationNumber);
}
