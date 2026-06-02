package pl.edu.pb.smuggling.user.model.dto;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String newPassword;
}
