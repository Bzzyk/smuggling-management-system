package pl.edu.pb.smuggling.user.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class PasswordResetRequest {
    @NotBlank(message = "Hasło jest wymagane")
    @Size(min = 4, message = "Hasło musi mieć co najmniej 4 znaki")
    private String newPassword;
}
