package pl.edu.pb.smuggling.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.edu.pb.smuggling.user.model.User;
import pl.edu.pb.smuggling.user.service.RoleService;
import pl.edu.pb.smuggling.user.service.UserService;

import java.util.Set;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }

    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Integer id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/users";
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "users/form";
    }

    @PostMapping("/{id}/edit")
    public String editUserRoles(@PathVariable Integer id, @RequestParam(required = false) Set<Integer> roleIds, RedirectAttributes redirectAttributes) {
        if (roleIds == null) {
            roleIds = Set.of();
        }
        userService.updateUserRoles(id, roleIds);
        redirectAttributes.addFlashAttribute("success", "Zaktualizowano role użytkownika.");
        return "redirect:/users";
    }

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
}
