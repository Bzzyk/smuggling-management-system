package pl.edu.pb.smuggling.common.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.edu.pb.smuggling.common.repository.AuthLogRepository;

@Controller
@RequestMapping("/auth-logs")
@RequiredArgsConstructor
public class AuthLogController {

    private final AuthLogRepository authLogRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listAuthLogs(Model model) {
        model.addAttribute("authLogs", authLogRepository.findAllByOrderByAttemptedAtDesc());
        return "audit/auth-list";
    }
}
