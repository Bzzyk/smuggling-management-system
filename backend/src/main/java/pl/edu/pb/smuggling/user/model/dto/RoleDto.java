package pl.edu.pb.smuggling.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.pb.smuggling.user.model.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private Integer id;
    private String name;
    private String description;

    public static RoleDto fromEntity(Role role) {
        if (role == null) return null;
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }
}
