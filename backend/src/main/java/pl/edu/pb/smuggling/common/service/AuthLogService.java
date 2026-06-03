package pl.edu.pb.smuggling.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pb.smuggling.common.model.AuthLog;
import pl.edu.pb.smuggling.common.repository.AuthLogRepository;
import pl.edu.pb.smuggling.user.model.User;
import pl.edu.pb.smuggling.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthLogService {

    private final AuthLogRepository authLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public void logAuthEvent(String username, String ipAddress, String userAgent, String status) {
        AuthLog authLog = new AuthLog();
        authLog.setUsername(username);
        authLog.setIpAddress(ipAddress);
        authLog.setUserAgent(userAgent);
        authLog.setStatus(status);

        if (username != null && !username.isBlank()) {
            User user = userRepository.findByUsername(username).orElse(null);
            authLog.setUser(user);
        }

        authLogRepository.save(authLog);
    }
}
