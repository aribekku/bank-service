package application.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
@AllArgsConstructor
public class CreateTransactionDTO {

    private Long accountFrom;

    private Long accountTo;

    private String currencyShortName;

    private Double sum;

    private String expenseCategory;
}
