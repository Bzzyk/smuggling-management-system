package pl.edu.pb.smuggling.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;
import pl.edu.pb.smuggling.user.model.User;
import pl.edu.pb.smuggling.user.model.dto.UserDto;
import pl.edu.pb.smuggling.user.model.dto.UserCreateRequest;
import pl.edu.pb.smuggling.user.model.dto.UserUpdateRequest;
import pl.edu.pb.smuggling.user.model.dto.PasswordResetRequest;
import pl.edu.pb.smuggling.user.service.UserService;
import jakarta.validation.Valid;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 200);
        Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<UserDto> users = userService.getAllUsers(PageRequest.of(safePage, safeSize, Sort.by(direction, sort)))
                .map(UserDto::fromEntity);
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserDto.fromEntity(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        
        boolean success = userService.registerUser(user, request.getRawPassword(), request.getRoleIds());
        if (!success) {
            return ResponseEntity.badRequest().build();
        }
        
        // Fetch the newly created user to return full DTO with mapped roles
        User createdUser = userService.getUserById(user.getId());
        return ResponseEntity.status(201).body(UserDto.fromEntity(createdUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Integer id, @Valid @RequestBody UserUpdateRequest request) {
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        
        Set<Integer> roleIds = request.getRoleIds() != null ? request.getRoleIds() : Set.of();
        userService.updateUser(id, request.getUsername(), request.getFirstName(), request.getLastName(), request.getEmail(), roleIds);
        
        User updatedUser = userService.getUserById(id);
        return ResponseEntity.ok(UserDto.fromEntity(updatedUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/toggle-ban")
    public ResponseEntity<Void> toggleBan(@PathVariable Integer id) {
        boolean success = userService.toggleUserBan(id);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build(); // Cannot ban admin
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable Integer id, @Valid @RequestBody PasswordResetRequest request) {
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        
        userService.resetPassword(id, request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
