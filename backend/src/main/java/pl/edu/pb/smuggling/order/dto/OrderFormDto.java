package pl.edu.pb.smuggling.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

@Data
public class OrderFormDto {

    private Integer id;

    @NotBlank(message = "Tytuł zlecenia jest wymagany")
    @Size(max = 100, message = "Tytuł może mieć maksymalnie 100 znaków")
    private String title;

    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate plannedDate;

    @NotNull(message = "Status zlecenia jest wymagany")
    private Integer statusId;

    private Integer responsibleUserId;

    @NumberFormat(pattern = "#.##")
    private BigDecimal estimatedProfit;
}
