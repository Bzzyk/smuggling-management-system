package pl.edu.pb.smuggling.cargo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pb.smuggling.cargo.model.Cargo;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Integer> {
}
