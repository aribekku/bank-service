package application.services;

import java.math.BigDecimal;

public interface CurrencyRateService {

    BigDecimal getCurrencyRate(String currency);
}
