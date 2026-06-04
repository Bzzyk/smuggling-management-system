package pl.edu.pb.smuggling.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentFormDto {

    private Integer id;

    @NotNull(message = "Zlecenie jest wymagane")
    private Integer orderId;

    @NotNull(message = "Kwota jest wymagana")
    @DecimalMin(value = "0.01", message = "Kwota musi być większa od 0")
    @NumberFormat(pattern = "#.##")
    private BigDecimal amount;

    @NotBlank(message = "Typ płatności jest wymagany")
    private String paymentType;

    @NotNull(message = "Status płatności jest wymagany")
    private Integer statusId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Data płatności nie może być z przyszłości")
    private LocalDate paymentDate;

    private String description;

    @AssertTrue(message = "Data płatności nie może być wcześniejsza niż 2000-01-01")
    public boolean isPaymentDateNotBeforeMinimum() {
        return paymentDate == null || !paymentDate.isBefore(LocalDate.of(2000, 1, 1));
    }
}
