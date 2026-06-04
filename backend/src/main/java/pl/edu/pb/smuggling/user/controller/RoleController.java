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
import pl.edu.pb.smuggling.user.model.Role;
import pl.edu.pb.smuggling.user.service.RoleService;

@Controller
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public String listRoles(Model model) {
        model.addAttribute("roles", roleService.getAllRoles());
        return "roles/list";
    }

    @GetMapping("/{id}/edit")
    public String editRoleForm(@PathVariable Integer id, Model model) {
        Role role = roleService.getRoleById(id);
        if (role == null) {
            return "redirect:/roles";
        }
        model.addAttribute("role", role);
        return "roles/form";
    }

    @PostMapping("/{id}/edit")
    public String editRoleDescription(@PathVariable Integer id, @RequestParam String description, RedirectAttributes redirectAttributes) {
        roleService.updateRoleDescription(id, description);
        redirectAttributes.addFlashAttribute("success", "Zaktualizowano opis roli.");
        return "redirect:/roles";
    }
}
