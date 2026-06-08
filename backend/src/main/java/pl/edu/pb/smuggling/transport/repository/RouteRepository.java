package pl.edu.pb.smuggling.transport.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pb.smuggling.transport.model.Route;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Integer> {

    boolean existsByName(String name);

    Optional<Route> findByName(String name);

    List<Route> findByActiveTrue();

    Page<Route> findByActiveTrue(Pageable pageable);
}
