package application.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SetNewLimitDTO {

    @Min(value = 1, message = "Limit sum can not be 0 or less")
    private Double limit;

    @NotBlank(message = "Expense category is required")
    private String expenseCategory;
}
