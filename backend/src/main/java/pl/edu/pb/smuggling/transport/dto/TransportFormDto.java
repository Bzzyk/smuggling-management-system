package pl.edu.pb.smuggling.transport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class TransportFormDto {

    private Integer id;

    @NotNull(message = "Zlecenie przemytu jest wymagane")
    private Integer orderId;

    private Integer routeId;

    @NotBlank(message = "Punkt startowy jest wymagany")
    @Size(max = 100, message = "Punkt startowy może mieć maksymalnie 100 znaków")
    private String startLocation;

    @NotBlank(message = "Miejsce docelowe jest wymagane")
    @Size(max = 100, message = "Miejsce docelowe może mieć maksymalnie 100 znaków")
    private String destination;

    @NotNull(message = "Data transportu jest wymagana")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate transportDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate plannedArrivalDate;

    private String description;
}
