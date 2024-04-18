package application.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "limits")
public class MonthlyLimit extends BaseEntity {

    private LocalDateTime limitSettingDate;

    private Double limitAmount;

    private Double limitBalance;

    private String currencyShortName;

    private String expenseCategory;

    @OneToOne
    private Transaction transaction;

    private Boolean limitExceeded;

}
