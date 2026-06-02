package pl.edu.pb.smuggling.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pb.smuggling.user.model.Role;
import pl.edu.pb.smuggling.user.repository.RoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Integer id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Transactional
    public void updateRoleDescription(Integer roleId, String description) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role != null) {
            role.setDescription(description);
            roleRepository.save(role);
        }
    }
}
