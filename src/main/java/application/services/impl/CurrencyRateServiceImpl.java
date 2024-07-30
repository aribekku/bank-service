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
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class CurrencyRateServiceImpl implements CurrencyRateService {
    private final CurrencyRateRepository currencyRateRepository;
    private final UUIDGenerator generator;

    @Autowired
    public CurrencyRateServiceImpl(CurrencyRateRepository currencyRateRepository, UUIDGenerator generator) {
        this.currencyRateRepository = currencyRateRepository;
        this.generator = generator;
    }

    @Override
    public double getCurrencyRate(String currency) {

        double rate;

        if (currencyRateRepository.existsByCurrencyAndCreatedAfter(currency, LocalDateTime.now().minusDays(1))) {
            CurrencyRate currencyRate = currencyRateRepository.findTopByCurrencyOrderByCreatedDesc(currency).orElse(new CurrencyRate());
            rate = currencyRate.getRate();
        }
        else {

            try {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("https://openexchangerates.org/api/latest.json?app_id=87ddf0ba0ca34854aa9aef7e7f1b018d")
                        .get()
                        .addHeader("accept", "application/json")
                        .build();

                Response response = client.newCall(request).execute();

                String stringResponse = response.body().string();
                JSONObject jsonObject = new JSONObject(stringResponse);
                JSONObject ratesObject = jsonObject.getJSONObject("rates");

                rate = ratesObject.getDouble(currency.toUpperCase());

                CurrencyRate currencyRate = new CurrencyRate();
                currencyRate.setId(generator);
                currencyRate.setCurrency(currency);
                currencyRate.setRate(rate);
                currencyRate.setCreated();

                currencyRateRepository.save(currencyRate);

            } catch (Exception exception) {
                throw new JSONException(exception.getMessage());
            }
        }

        return rate;
    }
}
