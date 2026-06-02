package pl.edu.pb.smuggling.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pb.smuggling.user.model.Role;
import pl.edu.pb.smuggling.user.model.User;
import pl.edu.pb.smuggling.user.repository.RoleRepository;
import pl.edu.pb.smuggling.user.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return false;
        }

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            return false;
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Invalidate sessions
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (principal instanceof UserDetails userDetails) {
                if (userDetails.getUsername().equals(username)) {
                    for (SessionInformation session : sessionRegistry.getAllSessions(principal, false)) {
                        session.expireNow();
                    }
                }
            }
        }

        return true;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public void resetPassword(Integer userId, String newPassword) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }

    @Transactional
    public void updateUser(Integer userId, String newUsername, String firstName, String lastName, String email, Set<Integer> roleIds) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setUsername(newUsername);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);

            boolean isAdmin = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ADMIN"));

            if (!isAdmin) {
                Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
                user.setRoles(roles);
            }
            userRepository.save(user);
        }
    }

    @Transactional
    public boolean registerUser(User user, String rawPassword, Set<Integer> roleIds) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return false;
        }
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        user.setRoles(roles);
        userRepository.save(user);
        return true;
    }

    @Transactional
    public boolean toggleUserBan(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            // Check if user is admin
            boolean isAdmin = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ADMIN"));
            if (isAdmin) {
                return false; // Cannot ban admin
            }
            user.setEnabled(!user.isEnabled());
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
