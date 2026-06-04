package pl.edu.pb.smuggling.transport.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RouteFormDto {

    private Integer id;

    @NotBlank(message = "Nazwa trasy jest wymagana")
    @Size(min = 3, max = 100, message = "Nazwa musi mieć od 3 do 100 znaków")
    private String name;

    @NotBlank(message = "Punkt początkowy jest wymagany")
    @Size(max = 100, message = "Punkt początkowy może mieć maksymalnie 100 znaków")
    private String startPoint;

    @NotBlank(message = "Punkt końcowy jest wymagany")
    @Size(max = 100, message = "Punkt końcowy może mieć maksymalnie 100 znaków")
    private String endPoint;

    @DecimalMin(value = "0.1", message = "Dystans musi być większy od 0")
    private BigDecimal distanceKm;

    @NotNull(message = "Poziom trudności jest wymagany")
    private Integer difficultyLevelId;

    private String description;
}
