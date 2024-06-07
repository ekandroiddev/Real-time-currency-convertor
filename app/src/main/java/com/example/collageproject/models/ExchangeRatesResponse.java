package com.example.collageproject.models;

import java.util.Map;

public class ExchangeRatesResponse {
    private String base;
    private Map<String, Double> rates;

    public String getBase() {
        return base;
    }

    public Map<String, Double> getRates() {
        return rates;
    }
}
