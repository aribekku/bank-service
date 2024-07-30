package application.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "limits")
public class MonthlyLimit extends BaseEntity {

    private LocalDateTime limitSettingDate;

    private BigDecimal limitAmount;

    private BigDecimal limitBalance;

    private String currencyShortName;

    private String expenseCategory;

    private boolean active;
}
