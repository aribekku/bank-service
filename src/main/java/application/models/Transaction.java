package application.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    private Long accountFrom;

    private Long accountTo;

    private String currencyShortName;

    private Double sum;

}
