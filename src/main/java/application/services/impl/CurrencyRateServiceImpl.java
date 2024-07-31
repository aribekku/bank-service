package application.services.impl;

import application.models.CurrencyRate;
import application.repositories.CurrencyRateRepository;
import application.services.CurrencyRateService;
import application.utils.UUIDGenerator;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class CurrencyRateServiceImpl implements CurrencyRateService {
    private final CurrencyRateRepository currencyRateRepository;
    private final UUIDGenerator generator;

    private final OkHttpClient httpClient;
    private final Clock clock;

    @Value("${currency.api.url}")
    private String apiUrl;

    @Autowired
    public CurrencyRateServiceImpl(CurrencyRateRepository currencyRateRepository, UUIDGenerator generator, OkHttpClient httpClient, Clock clock) {
        this.currencyRateRepository = currencyRateRepository;
        this.generator = generator;
        this.httpClient = httpClient;
        this.clock = clock;
    }

    @Override
    public BigDecimal getCurrencyRate(String currency) {

        BigDecimal rate;

        if (currencyRateRepository.existsByCurrencyAndCreatedAfter(currency, LocalDateTime.now().minusDays(1))) {
            CurrencyRate currencyRate = currencyRateRepository.findTopByCurrencyOrderByCreatedDesc(currency)
                    .orElseThrow(() -> new IllegalStateException("Currency rate not found"));
            rate = currencyRate.getRate();
        }
        else {

            rate = fetchAndSaveCurrencyRate(currency);
        }

        return rate;
    }

    private BigDecimal fetchAndSaveCurrencyRate(String currency) {
        try {
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .get()
                    .addHeader("accept", "application/json")
                    .build();

            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to fetch currency rate");
            }

            String stringResponse = response.body().string();
            JSONObject jsonObject = new JSONObject(stringResponse);
            JSONObject ratesObject = jsonObject.getJSONObject("rates");

            BigDecimal rate = ratesObject.getBigDecimal(currency.toUpperCase());

            CurrencyRate currencyRate = new CurrencyRate();
            currencyRate.setId(generator);
            currencyRate.setCurrency(currency);
            currencyRate.setRate(rate);
            currencyRate.setCreated(LocalDateTime.now(clock));

            currencyRateRepository.save(currencyRate);

            return rate;

        } catch (IOException exception) {
            throw new RuntimeException("Failed to fetch currency rate due to network error");
        } catch (JSONException exception) {
            throw new RuntimeException("Failed to parse " + currency + " currency rate response");
        } catch (Exception exception) {
            throw new RuntimeException("An unexpected error occurred while fetching currency rate");
        }
    }
}
