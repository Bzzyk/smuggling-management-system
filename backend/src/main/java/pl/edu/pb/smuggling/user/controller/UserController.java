package pl.edu.pb.smuggling.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import pl.edu.pb.smuggling.user.model.User;
import pl.edu.pb.smuggling.user.model.dto.UserCreateRequest;
import pl.edu.pb.smuggling.user.model.dto.UserUpdateRequest;
import pl.edu.pb.smuggling.user.model.dto.PasswordResetRequest;
import pl.edu.pb.smuggling.user.service.RoleService;
import pl.edu.pb.smuggling.user.service.UserService;

import java.util.Set;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Integer id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/users";
        }
        model.addAttribute("user", user);
        
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName(user.getFirstName());
        request.setLastName(user.getLastName());
        request.setUsername(user.getUsername());
        request.setEmail(user.getEmail());
        Set<Integer> roleIds = new java.util.HashSet<>();
        user.getRoles().forEach(r -> roleIds.add(r.getId()));
        request.setRoleIds(roleIds);
        
        model.addAttribute("request", request);
        model.addAttribute("resetRequest", new PasswordResetRequest());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "users/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/edit")
    public String editUser(@PathVariable Integer id, @Valid @ModelAttribute("request") UserUpdateRequest request, BindingResult result, Model model, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            User user = userService.getUserById(id);
            if (user == null) {
                return "redirect:/users";
            }
            model.addAttribute("user", user);
            model.addAttribute("resetRequest", new PasswordResetRequest());
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "users/form";
        }

        Set<Integer> roleIds = request.getRoleIds() != null ? request.getRoleIds() : Set.of();
        userService.updateUser(id, request.getUsername(), request.getFirstName(), request.getLastName(), request.getEmail(), roleIds);
        redirectAttributes.addFlashAttribute("success", "Zaktualizowano dane użytkownika.");
        return "redirect:/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("request", new UserCreateRequest());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "users/create";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("request") UserCreateRequest request, BindingResult result, Model model, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "users/create";
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        
        Set<Integer> roleIds = request.getRoleIds() != null ? request.getRoleIds() : Set.of();
        boolean success = userService.registerUser(user, request.getRawPassword(), roleIds);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Zarejestrowano nowego użytkownika.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Nazwa użytkownika jest już zajęta.");
        }
        return "redirect:/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/toggle-ban")
    public String toggleBan(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        boolean success = userService.toggleUserBan(id);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Zmieniono status użytkownika.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Nie można zbanować administratora.");
        }
        return "redirect:/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reset-password")
    public String resetPassword(@PathVariable Integer id, @Valid @ModelAttribute("resetRequest") PasswordResetRequest request, BindingResult result, Model model, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            User user = userService.getUserById(id);
            if (user == null) {
                return "redirect:/users";
            }
            model.addAttribute("user", user);
            
            UserUpdateRequest mainRequest = new UserUpdateRequest();
            mainRequest.setFirstName(user.getFirstName());
            mainRequest.setLastName(user.getLastName());
            mainRequest.setUsername(user.getUsername());
            mainRequest.setEmail(user.getEmail());
            Set<Integer> roleIds = new java.util.HashSet<>();
            user.getRoles().forEach(r -> roleIds.add(r.getId()));
            mainRequest.setRoleIds(roleIds);
            
            model.addAttribute("request", mainRequest);
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "users/form";
        }

        userService.resetPassword(id, request.getNewPassword());
        redirectAttributes.addFlashAttribute("success", "Hasło użytkownika zostało zresetowane.");
        return "redirect:/users";
    }
}
