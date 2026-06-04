package pl.edu.pb.smuggling.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.edu.pb.smuggling.common.service.AuthLogService;

@Component
@RequiredArgsConstructor
public class AuthenticationEventListener {

    private final AuthLogService authLogService;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        logEvent(username, "SUCCESS");
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        logEvent(username, "FAILURE");
    }
    
    @EventListener
    public void onLogout(LogoutSuccessEvent event) {
        if (event.getAuthentication() != null) {
            String username = event.getAuthentication().getName();
            logEvent(username, "LOGOUT");
        }
    }

    private void logEvent(String username, String status) {
        HttpServletRequest request = getCurrentHttpRequest();
        String ipAddress = null;
        String userAgent = null;

        if (request != null) {
            ipAddress = getClientIp(request);
            userAgent = request.getHeader("User-Agent");
        }

        authLogService.logAuthEvent(username, ipAddress, userAgent, status);
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        String xrHeader = request.getHeader("X-Real-IP");
        if (xrHeader != null && !xrHeader.isEmpty()) {
            return xrHeader.trim();
        }
        return request.getRemoteAddr();
    }
}
