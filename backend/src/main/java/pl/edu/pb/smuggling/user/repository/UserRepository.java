package pl.edu.pb.smuggling.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pb.smuggling.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);
}
