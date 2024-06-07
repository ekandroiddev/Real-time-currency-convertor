package com.example.collageproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collageproject.adapters.RecyclerViewAdapter;
import com.example.collageproject.fragments.ConversionFragment;
import com.example.collageproject.models.CurrencyModel;
import com.example.collageproject.utils.FlagUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllCurrencyViewActivity extends AppCompatActivity implements OnItemClickListener {
    RecyclerView recyclerView;
    List<CurrencyModel> currencyModelList = new ArrayList<>();
    String currencyNature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_currency_view);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), currencyModelList, (OnItemClickListener) this);
        recyclerView.setAdapter(recyclerViewAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#1f88fc"));

        Intent intent = getIntent();
        currencyNature = intent.getStringExtra("currencyNature");

        ExchangeRatesApi apiService = RetrofitClient.getRetrofitInstance().create(ExchangeRatesApi.class);
        Call<Map<String, String>> call = apiService.getCurrencies();

        call.enqueue(new Callback<Map<String, String>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<Map<String, String>> call, @NonNull Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Map<String, String> currencies = response.body();
                    if (currencies != null) {

                        Map<String, String> currencyFlags = FlagUtil.getCurrencysFlag();
                        for (Map.Entry<String, String> entry : currencies.entrySet()) {
                            String code = entry.getKey();
                            String name = entry.getValue();
                            String flagURL = currencyFlags.getOrDefault(code, "");
                            currencyModelList.add(new CurrencyModel(code, name, flagURL));
                        }
                        recyclerViewAdapter.notifyDataSetChanged();

                    } else {
                        Log.e("MainActivity", "Request failed: " + response.message());

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                Log.e("MainActivity", "Network error: " + t.getMessage());
            }
        });
    }

    @Override
    public void onItemClicks(String code, String flagURL) {
        Intent intent = new Intent(AllCurrencyViewActivity.this, MainActivity.class);
        intent.putExtra("openFragment", "ConversionFragment");
        intent.putExtra("code", code);
        intent.putExtra("flagURL", flagURL);
        intent.putExtra("nature", currencyNature);
        startActivity(intent);
    }

}