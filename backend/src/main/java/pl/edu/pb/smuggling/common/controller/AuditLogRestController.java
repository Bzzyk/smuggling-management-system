package pl.edu.pb.smuggling.common.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pb.smuggling.common.dto.AuditLogDto;
import pl.edu.pb.smuggling.common.model.AuditLog;
import pl.edu.pb.smuggling.common.repository.AuditLogRepository;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogRestController {

    private final AuditLogRepository auditLogRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<AuditLogDto>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        // Prevent huge queries
        int safeSize = Math.min(size, 500);
        
        Page<AuditLog> logPage = auditLogRepository.findAllByOrderByChangedAtDesc(PageRequest.of(page, safeSize));
        Page<AuditLogDto> dtoPage = logPage.map(AuditLogDto::fromEntity);
        
        return ResponseEntity.ok(dtoPage);
    }
}
