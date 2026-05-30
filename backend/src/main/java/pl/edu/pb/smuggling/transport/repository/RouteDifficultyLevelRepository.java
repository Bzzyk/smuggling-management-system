package pl.edu.pb.smuggling.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pb.smuggling.transport.model.RouteDifficultyLevel;

import java.util.Optional;

public interface RouteDifficultyLevelRepository extends JpaRepository<RouteDifficultyLevel, Integer> {

    Optional<RouteDifficultyLevel> findByName(String name);
}
