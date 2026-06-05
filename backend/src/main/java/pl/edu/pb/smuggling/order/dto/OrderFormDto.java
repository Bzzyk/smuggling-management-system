package pl.edu.pb.smuggling.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OrderFormDto {

    private Integer id;

    @NotBlank(message = "Tytul zlecenia jest wymagany")
    @Size(min = 3, max = 100, message = "Tytul musi miec od 3 do 100 znakow")
    private String title;

    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate plannedDate;

    @NotNull(message = "Status zlecenia jest wymagany")
    private Integer statusId;

    private Integer responsibleUserId;

    @DecimalMin(value = "0.00", message = "Szacowany zysk nie moze byc ujemny")
    @NumberFormat(pattern = "#.##")
    private BigDecimal estimatedProfit;
}
