package pl.edu.pb.smuggling.transport.dto.rest;

import lombok.Builder;
import lombok.Data;
import pl.edu.pb.smuggling.transport.model.SmugglerAssignment;

import java.time.LocalDateTime;

@Data
@Builder
public class SmugglerAssignmentDto {
    private Integer id;
    private Integer transportId;
    private Integer smugglerId;
    private String smugglerName;
    private String experienceLevel;
    private LocalDateTime assignedAt;
    private boolean active;
    private String note;

    public static SmugglerAssignmentDto fromEntity(SmugglerAssignment assignment) {
        return SmugglerAssignmentDto.builder()
                .id(assignment.getId())
                .transportId(assignment.getTransport() != null ? assignment.getTransport().getId() : null)
                .smugglerId(assignment.getSmuggler() != null ? assignment.getSmuggler().getUserId() : null)
                .smugglerName(assignment.getSmuggler() != null && assignment.getSmuggler().getUser() != null
                        ? assignment.getSmuggler().getUser().getFirstName() + " " + assignment.getSmuggler().getUser().getLastName()
                        : null)
                .experienceLevel(assignment.getSmuggler() != null ? assignment.getSmuggler().getExperienceLevel() : null)
                .assignedAt(assignment.getAssignedAt())
                .active(assignment.isActive())
                .note(assignment.getNote())
                .build();
    }
}
