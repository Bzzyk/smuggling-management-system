package pl.edu.pb.smuggling.transport.dto.rest;

import lombok.Builder;
import lombok.Data;
import pl.edu.pb.smuggling.transport.model.RouteDifficultyLevel;

@Data
@Builder
public class RouteDifficultyLevelDto {
    private Integer id;
    private String name;
    private Integer riskLevel;
    private String description;

    public static RouteDifficultyLevelDto fromEntity(RouteDifficultyLevel level) {
        return RouteDifficultyLevelDto.builder()
                .id(level.getId())
                .name(level.getName())
                .riskLevel(level.getRiskLevel())
                .description(level.getDescription())
                .build();
    }
}
