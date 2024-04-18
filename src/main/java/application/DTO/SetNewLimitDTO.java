package application.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class SetNewLimitDTO {

    private Double limit;

    private String expenseCategory;
}
