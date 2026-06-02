package pl.edu.pb.smuggling.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.access.prepost.PreAuthorize;
import pl.edu.pb.smuggling.user.model.User;
import pl.edu.pb.smuggling.user.model.dto.UserDto;
import pl.edu.pb.smuggling.user.model.dto.UserCreateRequest;
import pl.edu.pb.smuggling.user.model.dto.UserUpdateRequest;
import pl.edu.pb.smuggling.user.model.dto.PasswordResetRequest;
import pl.edu.pb.smuggling.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

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
    public ResponseEntity<UserDto> createUser(@RequestBody UserCreateRequest request) {
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
    public ResponseEntity<UserDto> updateUser(@PathVariable Integer id, @RequestBody UserUpdateRequest request) {
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        
        userService.updateUser(id, request.getUsername(), request.getFirstName(), request.getLastName(), request.getEmail(), request.getRoleIds());
        
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
    public ResponseEntity<Void> resetPassword(@PathVariable Integer id, @RequestBody PasswordResetRequest request) {
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        
        userService.resetPassword(id, request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
