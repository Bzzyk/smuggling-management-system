package pl.edu.pb.smuggling.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pb.smuggling.user.model.SmugglerProfile;

public interface SmugglerProfileRepository extends JpaRepository<SmugglerProfile, Integer> {
}
