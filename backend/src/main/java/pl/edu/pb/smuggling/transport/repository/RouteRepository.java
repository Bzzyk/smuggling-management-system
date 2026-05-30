package pl.edu.pb.smuggling.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pb.smuggling.transport.model.Route;

public interface RouteRepository extends JpaRepository<Route, Integer> {

    boolean existsByName(String name);
}
