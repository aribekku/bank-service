package application.repositories;

import application.models.CurrencyRate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CurrencyRateRepository extends MongoRepository<CurrencyRate, String> {

    Boolean existsByCurrencyAndCreatedAfter(String currency, LocalDateTime date);

    Optional<CurrencyRate> findTopByCurrencyOrderByCreatedDesc(String currency);
}
