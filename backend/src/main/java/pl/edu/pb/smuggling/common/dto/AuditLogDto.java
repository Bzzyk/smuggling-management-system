package pl.edu.pb.smuggling.common.dto;

import lombok.Builder;
import lombok.Data;
import pl.edu.pb.smuggling.common.model.AuditLog;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogDto {
    private Integer id;
    private String tableName;
    private Integer recordId;
    private String action;
    private String changedByUsername;
    private LocalDateTime changedAt;
    private String oldValue;
    private String newValue;

    public static AuditLogDto fromEntity(AuditLog log) {
        return AuditLogDto.builder()
                .id(log.getId())
                .tableName(log.getTableName())
                .recordId(log.getRecordId())
                .action(log.getAction())
                .changedByUsername(log.getChangedBy() != null ? log.getChangedBy().getUsername() : "SYSTEM")
                .changedAt(log.getChangedAt())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .build();
    }
}
