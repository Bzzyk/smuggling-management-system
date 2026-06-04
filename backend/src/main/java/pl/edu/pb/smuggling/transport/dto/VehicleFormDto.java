package pl.edu.pb.smuggling.transport.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import pl.edu.pb.smuggling.transport.model.VehicleType;

@Data
public class VehicleFormDto {

    private Integer id;

    @NotBlank(message = "Numer rejestracyjny jest wymagany")
    @Size(max = 20, message = "Numer rejestracyjny może mieć maksymalnie 20 znaków")
    private String registrationNumber;

    @NotBlank(message = "Marka jest wymagana")
    @Size(max = 50, message = "Marka może mieć maksymalnie 50 znaków")
    private String brand;

    @NotBlank(message = "Model jest wymagany")
    @Size(max = 50, message = "Model może mieć maksymalnie 50 znaków")
    private String model;

    @NotNull(message = "Typ pojazdu jest wymagany")
    private VehicleType vehicleType;

    @NotNull(message = "Ładowność jest wymagana")
    @Min(value = 1, message = "Ładowność musi być większa od zera")
    private Integer loadCapacity;

    @NotNull(message = "Status dostępności jest wymagany")
    private Boolean available = true;
}
