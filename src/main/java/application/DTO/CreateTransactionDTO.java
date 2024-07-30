package application.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor
public class CreateTransactionDTO {

    @NotNull
    private Long accountFrom;

    @NotNull
    private Long accountTo;

    @Size(max = 5, message = "Currency short name must be not more than 5 characters")
    private String currencyShortName;

    @NotEmpty
    @Min(1)
    private BigDecimal sum;

    @NotBlank(message = "Expense category is required")
    private String expenseCategory;
}
