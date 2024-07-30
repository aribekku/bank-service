package application.configuration;

import com.squareup.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AppConfiguration {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
