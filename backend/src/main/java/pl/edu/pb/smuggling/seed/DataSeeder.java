package pl.edu.pb.smuggling.seed;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pb.smuggling.user.model.Role;
import pl.edu.pb.smuggling.user.model.User;
import pl.edu.pb.smuggling.user.repository.RoleRepository;
import pl.edu.pb.smuggling.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedAdminUser() {
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
                Role role = new Role();
                role.setName("ADMIN");
                role.setDescription("Administrator systemu");
                return roleRepository.save(role);
            });

            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("Systemowy");
            admin.setUsername("admin");
            admin.setEmail("admin@smuggling.local");
            admin.setPasswordHash(passwordEncoder.encode("admin"));
            admin.getRoles().add(adminRole);
            
            userRepository.save(admin);
            
            System.out.println("Utworzono domyślnego użytkownika: admin / admin");
        }
    }
}
