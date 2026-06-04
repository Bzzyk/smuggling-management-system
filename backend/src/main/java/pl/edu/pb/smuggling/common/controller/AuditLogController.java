package pl.edu.pb.smuggling.common.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.edu.pb.smuggling.common.repository.AuditLogRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;
import pl.edu.pb.smuggling.common.model.AuditLog;

@Controller
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        
        Page<AuditLog> logPage = auditLogRepository.findAllByOrderByChangedAtDesc(PageRequest.of(page, 50));
        model.addAttribute("auditLogs", logPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", logPage.getTotalPages());
        
        return "audit/list";
    }
}
