package pl.edu.pb.smuggling.transport.dto.rest;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeTransportStatusRequest {
    @NotBlank(message = "Status transportu jest wymagany")
    private String statusName;
}
