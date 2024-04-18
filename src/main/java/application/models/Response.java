package application.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private String disclaimer;
    private String license;
    private String timestamp;
    private String base;
    private LinkedHashMap<String, Double> rates;
}
