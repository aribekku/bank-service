package application.currencyRate;

import application.models.CurrencyRate;
import application.repositories.CurrencyRateRepository;
import application.services.impl.CurrencyRateServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyRateServiceTest {

    @Mock
    private CurrencyRateRepository currencyRateRepository;

    @InjectMocks
    private CurrencyRateServiceImpl currencyRateService;

    @Test
    public void test_GetCurrencyRate_FromDatabase() {
        String currency = "USD";
        BigDecimal expectedRate = new BigDecimal("1.0");

        CurrencyRate currencyRate = new CurrencyRate();
        currencyRate.setCurrency(currency);
        currencyRate.setRate(expectedRate);
        currencyRate.setCreated(LocalDateTime.now().minusHours(1));

        when(currencyRateRepository.existsByCurrencyAndCreatedAfter(eq(currency), any(LocalDateTime.class)))
                .thenReturn(true);
        when(currencyRateRepository.findTopByCurrencyOrderByCreatedDesc(eq(currency)))
                .thenReturn(Optional.of(currencyRate));

        BigDecimal rate = currencyRateService.getCurrencyRate(currency);

        Assertions.assertEquals(expectedRate, rate);
    }
}
