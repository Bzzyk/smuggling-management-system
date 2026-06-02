package pl.edu.pb.smuggling.user.model.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserCreateRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String rawPassword;
    private Set<Integer> roleIds;
}
