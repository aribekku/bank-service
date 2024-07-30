package application.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    private long accountFrom;

    private long accountTo;

    private String currencyShortName;

    private BigDecimal sum;

    private String expenseCategory;

    @ManyToOne
    private MonthlyLimit limit;

    private boolean limitExceeded;
}
