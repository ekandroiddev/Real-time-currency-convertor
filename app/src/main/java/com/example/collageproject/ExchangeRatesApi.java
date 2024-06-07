package com.example.collageproject;

import com.example.collageproject.models.ExchangeRatesResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ExchangeRatesApi {
    @GET("latest.json")
    Call<ExchangeRatesResponse> getLatestRates(@Query("app_id") String appId);
    @GET("historical/{date}.json")
    Call<ExchangeRatesResponse> getHistoricalRates(@Path("date") String date, @Query("app_id") String appId);
    @GET("currencies.json")
    Call<Map<String, String>> getCurrencies();
}
