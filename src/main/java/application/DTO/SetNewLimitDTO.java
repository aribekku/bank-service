package application.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SetNewLimitDTO {

    private Double limit;

    private String expenseCategory;
}
