package pl.edu.pb.smuggling.transport.dto.rest;

import lombok.Builder;
import lombok.Data;
import pl.edu.pb.smuggling.transport.model.Route;

import java.math.BigDecimal;

@Data
@Builder
public class RouteDto {
    private Integer id;
    private String name;
    private String startPoint;
    private String endPoint;
    private BigDecimal distanceKm;
    private Integer difficultyLevelId;
    private String difficultyLevelName;
    private Integer riskLevel;
    private String description;
    private Boolean active;

    public static RouteDto fromEntity(Route route) {
        return RouteDto.builder()
                .id(route.getId())
                .name(route.getName())
                .startPoint(route.getStartPoint())
                .endPoint(route.getEndPoint())
                .distanceKm(route.getDistanceKm())
                .difficultyLevelId(route.getDifficultyLevel() != null ? route.getDifficultyLevel().getId() : null)
                .difficultyLevelName(route.getDifficultyLevel() != null ? route.getDifficultyLevel().getName() : null)
                .riskLevel(route.getDifficultyLevel() != null ? route.getDifficultyLevel().getRiskLevel() : null)
                .description(route.getDescription())
                .active(route.getActive())
                .build();
    }
}
