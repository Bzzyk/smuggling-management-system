package pl.edu.pb.smuggling.common.dto;

import lombok.Builder;
import lombok.Data;
import pl.edu.pb.smuggling.common.model.AuthLog;

import java.time.LocalDateTime;

@Data
@Builder
public class AuthLogDto {
    private Integer id;
    private Integer userId;
    private String username;
    private String ipAddress;
    private String userAgent;
    private String status;
    private LocalDateTime attemptedAt;

    public static AuthLogDto fromEntity(AuthLog log) {
        return AuthLogDto.builder()
                .id(log.getId())
                .userId(log.getUser() != null ? log.getUser().getId() : null)
                .username(log.getUsername())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .status(log.getStatus())
                .attemptedAt(log.getAttemptedAt())
                .build();
    }
}
