package pl.edu.pb.smuggling.common.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.edu.pb.smuggling.common.repository.AuthLogRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;
import pl.edu.pb.smuggling.common.model.AuthLog;

@Controller
@RequestMapping("/auth-logs")
@RequiredArgsConstructor
public class AuthLogController {

    private final AuthLogRepository authLogRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listAuthLogs(
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        
        Page<AuthLog> logPage = authLogRepository.findAllByOrderByAttemptedAtDesc(PageRequest.of(page, 50));
        model.addAttribute("authLogs", logPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", logPage.getTotalPages());
        
        return "audit/auth-list";
    }
}
