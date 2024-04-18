package application.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class GetTransactionDTO {

    private Long accountFrom;

    private Long accountTo;

    private String currencyShortName;

    private Double sum;

    private String expenseCategory;

    private LocalDateTime transactionDateTime;

    private Double limitSum;

    private LocalDateTime limitDateTime;

    private String limitCurrencyShortName;
}
