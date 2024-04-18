package application.models;

import application.utils.UUIDGenerator;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRate {

    @Id
    private String id;
    private LocalDateTime created;
    private String currency;
    private Double rate;

    public void setId(UUIDGenerator<CurrencyRate> generator) {
        this.id = generator.generate(this);
    }

    public void setCreated() {
        this.created = LocalDateTime.now();
    }

}
