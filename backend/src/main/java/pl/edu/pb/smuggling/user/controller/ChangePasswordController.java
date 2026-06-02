package pl.edu.pb.smuggling.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.edu.pb.smuggling.user.service.UserService;

@Controller
@RequestMapping("/change-password")
@RequiredArgsConstructor
public class ChangePasswordController {

    private final UserService userService;

    @GetMapping
    public String showChangePasswordForm() {
        return "change-password";
    }

    @PostMapping
    public String changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Authentication authentication,
            Model model) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Nowe hasła nie są identyczne.");
            return "change-password";
        }

        boolean success = userService.changePassword(authentication.getName(), oldPassword, newPassword);

        if (!success) {
            model.addAttribute("error", "Obecne hasło jest nieprawidłowe.");
            return "change-password";
        }

        model.addAttribute("success", true);
        return "change-password";
    }
}
