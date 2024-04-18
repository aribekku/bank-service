package application.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class CreateTransactionDTO {

    private Long accountFrom;

    private Long accountTo;

    private String currencyShortName;

    private Double sum;

    private String expenseCategory;
}
