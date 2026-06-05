package pl.edu.pb.smuggling.cargo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;

@Data
public class CargoFormDto {
    private Integer id;

    @NotBlank(message = "Nazwa ładunku jest wymagana")
    @Size(min = 3, max = 100, message = "Nazwa ładunku musi mieć od 3 do 100 znaków")
    private String name;

    @NotNull(message = "Typ ładunku jest wymagany")
    private Integer cargoTypeId;

    @NotNull(message = "Liczba paczek jest wymagana")
    @Min(value = 1, message = "Liczba paczek musi być większa od 0")
    private Integer packagesCount;

    @NotNull(message = "Szacowana wartość jest wymagana")
    @DecimalMin(value = "0.00", message = "Szacowana wartość nie może być ujemna")
    @NumberFormat(pattern = "#.##")
    private BigDecimal estimatedValue;

    private Integer warehouseId;
}
