package pl.edu.pb.smuggling.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pb.smuggling.common.model.AuditLog;
import pl.edu.pb.smuggling.common.repository.AuditLogRepository;
import pl.edu.pb.smuggling.user.model.User;
import pl.edu.pb.smuggling.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Transactional
    public void logAction(String tableName, Integer recordId, String action, Object oldValueObj, Object newValueObj) {
        String oldValueStr = null;
        String newValueStr = null;
        try {
            if (oldValueObj != null) {
                oldValueStr = oldValueObj instanceof String ? (String) oldValueObj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(oldValueObj);
            }
            if (newValueObj != null) {
                newValueStr = newValueObj instanceof String ? (String) newValueObj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(newValueObj);
            }
        } catch (Exception e) {
            oldValueStr = oldValueObj != null ? oldValueObj.toString() : null;
            newValueStr = newValueObj != null ? newValueObj.toString() : null;
        }
        logAction(tableName, recordId, action, oldValueStr, newValueStr);
    }

    @Transactional
    public void logAction(String tableName, Integer recordId, String action, String oldValue, String newValue) {
        AuditLog auditLog = new AuditLog();
        auditLog.setTableName(tableName);
        auditLog.setRecordId(recordId);
        auditLog.setAction(action);
        auditLog.setOldValue(oldValue);
        auditLog.setNewValue(newValue);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            User currentUser = userRepository.findByUsername(username).orElse(null);
            auditLog.setChangedBy(currentUser);
        }

        auditLogRepository.save(auditLog);
    }
}
