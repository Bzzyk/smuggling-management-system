package pl.edu.pb.smuggling.transport.dto.rest;

import lombok.Builder;
import lombok.Data;
import pl.edu.pb.smuggling.transport.model.TransportStatus;

@Data
@Builder
public class TransportStatusDto {
    private Integer id;
    private String name;
    private String description;

    public static TransportStatusDto fromEntity(TransportStatus status) {
        return TransportStatusDto.builder()
                .id(status.getId())
                .name(status.getName())
                .description(status.getDescription())
                .build();
    }
}
