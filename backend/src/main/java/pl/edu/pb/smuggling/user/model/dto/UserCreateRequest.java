package pl.edu.pb.smuggling.user.model.dto;

import lombok.Data;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class UserCreateRequest {
    @NotBlank(message = "Imię jest wymagane")
    @Size(min = 2, max = 50, message = "Imię musi mieć od 2 do 50 znaków")
    private String firstName;

    @NotBlank(message = "Nazwisko jest wymagane")
    @Size(min = 2, max = 80, message = "Nazwisko musi mieć od 2 do 80 znaków")
    private String lastName;

    @NotBlank(message = "Nazwa użytkownika jest wymagana")
    @Size(min = 3, max = 50, message = "Nazwa użytkownika musi mieć od 3 do 50 znaków")
    private String username;

    @NotBlank(message = "Adres e-mail jest wymagany")
    @Email(message = "Niepoprawny format adresu e-mail")
    @Size(max = 120, message = "Adres e-mail może mieć maksymalnie 120 znaków")
    private String email;

    @NotBlank(message = "Hasło jest wymagane")
    @Size(min = 4, message = "Hasło musi mieć co najmniej 4 znaki")
    private String rawPassword;

    private Set<Integer> roleIds;
}
