package pl.edu.pb.smuggling.cargo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WarehouseFormDto {
    private Integer id;

    @NotBlank(message = "Nazwa magazynu jest wymagana")
    @Size(min = 3, max = 100, message = "Nazwa magazynu musi mieć od 3 do 100 znaków")
    private String name;

    @NotBlank(message = "Lokalizacja jest wymagana")
    @Size(max = 150, message = "Lokalizacja może mieć maksymalnie 150 znaków")
    private String location;

    @NotNull(message = "Maksymalna pojemność jest wymagana")
    @Min(value = 1, message = "Maksymalna pojemność musi być większa od 0")
    private Integer maxCapacity;

    private boolean active = true;
}
