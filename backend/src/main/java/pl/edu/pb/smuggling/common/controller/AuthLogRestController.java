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
import pl.edu.pb.smuggling.common.dto.AuthLogDto;
import pl.edu.pb.smuggling.common.model.AuthLog;
import pl.edu.pb.smuggling.common.repository.AuthLogRepository;

@RestController
@RequestMapping("/api/auth-logs")
@RequiredArgsConstructor
public class AuthLogRestController {

    private final AuthLogRepository authLogRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<AuthLogDto>> getAuthLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        // Prevent huge queries
        int safeSize = Math.min(size, 500);
        
        Page<AuthLog> logPage = authLogRepository.findAllByOrderByAttemptedAtDesc(PageRequest.of(page, safeSize));
        Page<AuthLogDto> dtoPage = logPage.map(AuthLogDto::fromEntity);
        
        return ResponseEntity.ok(dtoPage);
    }
}
