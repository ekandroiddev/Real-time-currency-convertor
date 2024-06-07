package com.example.collageproject.models;

public class CurrencyModel {
    public String currencyCode;
    public String currencyName;
    public String flagUrl;

    public CurrencyModel(String currencyCode, String currencyName, String flagUrl) {
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
        this.flagUrl = flagUrl;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }
    public String getFlagUrl(){
        return flagUrl;
    }
}
