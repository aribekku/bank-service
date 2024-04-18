package application.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGenerator<TEntity> {
    public String generate(TEntity tEntity) {
        String id = UUID.randomUUID().toString();

        return switch (tEntity.getClass().getSimpleName()) {
            case "CurrencyRate" -> id;
            default -> throw new IllegalArgumentException();
        };
    }
}
