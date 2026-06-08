package pl.edu.pb.smuggling.transport.dto.rest;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignSmugglerRequest {
    @NotNull(message = "Przemytnik jest wymagany")
    private Integer smugglerId;

    private String note;
}
