package pl.edu.pb.smuggling.user.model.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private Set<Integer> roleIds;
}
