package application.services.impl;

import application.models.CurrencyRate;
import application.models.Response;
import application.repositories.CurrencyRateRepository;
import application.services.CurrencyRateService;
import application.utils.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@Service
public class CurrencyRateServiceImpl implements CurrencyRateService {
    private final CurrencyRateRepository currencyRateRepository;
    private final WebClient webClient;
    private final UUIDGenerator generator;

    @Autowired
    public CurrencyRateServiceImpl(CurrencyRateRepository currencyRateRepository, WebClient.Builder webClientBuilder, UUIDGenerator generator) {
        this.currencyRateRepository = currencyRateRepository;
        this.webClient = webClientBuilder.baseUrl("https://openexchangerates.org").build();
        this.generator = generator;
    }

    @Override
    public Double getCurrencyRate(String currency) {

        Double rate;

        if (currencyRateRepository.existsByCurrencyAndCreatedAfter(currency, LocalDateTime.now().minusDays(1))) {
            CurrencyRate currencyRate = currencyRateRepository.findByCurrencyAndCreatedAfter(currency, LocalDateTime.now().minusDays(1));
            rate = currencyRate.getRate();
        }
        else {
            Response response = webClient
                    .get()
                    .uri("/api/latest.json?app_id=87ddf0ba0ca34854aa9aef7e7f1b018d")
                    .retrieve()
                    .bodyToMono(Response.class).block();

            LinkedHashMap<String, Double> rates = response.getRates();

            CurrencyRate currencyRate = new CurrencyRate();

            currencyRate.setId(generator);
            currencyRate.setCurrency(currency);
            currencyRate.setRate(rates.get(currency));
            currencyRate.setCreated();

            currencyRateRepository.save(currencyRate);
            rate = rates.get(currency);
        }

        return rate;
    }
}
