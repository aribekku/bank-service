package application.repositories;

import application.models.CurrencyRate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;

public interface CurrencyRateRepository extends MongoRepository<CurrencyRate, String> {

    Boolean existsByCurrencyAndCreatedAfter(String currency, LocalDateTime date);

    CurrencyRate findByCurrencyAndCreatedAfter(String currency, LocalDateTime date);
}
