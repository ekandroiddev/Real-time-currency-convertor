package com.example.collageproject.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.collageproject.AllCurrencyViewActivity;
import com.example.collageproject.ExchangeRatesApi;
import com.example.collageproject.R;
import com.example.collageproject.RetrofitClient;
import com.example.collageproject.models.ExchangeRatesResponse;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatsFragment extends Fragment {

    private static final String TAG = "StatsFragment";
    private static final String API_KEY = "2b16efb060b6413eb2c500f5717f7cff";

    LinearLayout currencyFromLayout, currencyToLayout;
    ImageView flagFromCurrency, flagToCurrency, exchange;
    TextView fromCurrency_code, toCurrency_code;
    Button btnWeeklyView, btnMonthlyView;
    Intent intent;
    ExchangeRatesApi apiService;
    int noOfDays;
    LineChart lineChart;
    List<Entry> entries = new ArrayList<>();

    public StatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = RetrofitClient.getRetrofitInstance().create(ExchangeRatesApi.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        currencyFromLayout = view.findViewById(R.id.currencyFromLayout);
        currencyToLayout = view.findViewById(R.id.toCurrencyLayout);
        flagFromCurrency = view.findViewById(R.id.flagFromCurrency);
        fromCurrency_code = view.findViewById(R.id.fromCurrency_code);
        flagToCurrency = view.findViewById(R.id.flagToCurrency);
        toCurrency_code = view.findViewById(R.id.toCurrency_code);
        lineChart = view.findViewById(R.id.lineChart);
        exchange = view.findViewById(R.id.exchange);
        btnWeeklyView = view.findViewById(R.id.weekView);
        btnMonthlyView = view.findViewById(R.id.monthView);

        fetchHistoricalData(fromCurrency_code.getText().toString(), toCurrency_code.getText().toString(), 7);

        btnWeeklyView.setOnClickListener(historicChartView);
        btnMonthlyView.setOnClickListener(historicChartView);
        currencyFromLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getContext(), AllCurrencyViewActivity.class);
                intent.putExtra("currencyNature", "fromCurrency");
                startActivity(intent);
            }
        });
        currencyToLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getContext(), AllCurrencyViewActivity.class);
                intent.putExtra("currencyNature", "toCurrency");
                startActivity(intent);
            }
        });
        updateUIFromArguments(getArguments());

        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCurrencys();
            }
        });

        return view;
    }

    private void updateUIFromArguments(Bundle arguments) {
        if (arguments != null) {
            String code = arguments.getString("code");
            String flagUrl = arguments.getString("flagURL");
            String nature = arguments.getString("nature");

            int defaultFlagResource = R.drawable.deffaultflag;

            if ("fromCurrency".equals(nature)) {
                Log.d(TAG, "Updating fromCurrency UI components");
                fromCurrency_code.setText(code);
                if (flagUrl != null && !flagUrl.isEmpty()) {
                    Picasso.get().load(flagUrl).into(flagFromCurrency);
                } else {
                    flagFromCurrency.setBackgroundResource(defaultFlagResource);
                }
            } else if ("toCurrency".equals(nature)) {
                Log.d(TAG, "Updating toCurrency UI components");
                toCurrency_code.setText(code);
                if (flagUrl != null && !flagUrl.isEmpty()) {
                    Picasso.get().load(flagUrl).into(flagToCurrency);
                } else {
                    flagToCurrency.setBackgroundResource(defaultFlagResource);
                }
            }
        }
    }

    private final View.OnClickListener historicChartView = new View.OnClickListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            String buttonText = button.getText().toString();

            if (buttonText.equals("Week")) {
                btnWeeklyView.setBackgroundColor(R.color.blue);
                btnWeeklyView.setTextColor(R.color.white);
                btnMonthlyView.setBackgroundColor(R.color.bgcolor);
                btnMonthlyView.setTextColor(R.color.black);
                noOfDays = 7;
                fetchHistoricalData(fromCurrency_code.getText().toString(), toCurrency_code.getText().toString(), noOfDays);
                Toast.makeText(getContext(), "week view", Toast.LENGTH_SHORT).show();
            }
            if (buttonText.equals("Month")) {
                btnMonthlyView.setBackgroundColor(R.color.blue);
                btnMonthlyView.setTextColor(R.color.white);
                btnWeeklyView.setBackgroundColor(R.color.bgcolor);
                btnWeeklyView.setTextColor(R.color.black);
                noOfDays = 30;
                fetchHistoricalData(fromCurrency_code.getText().toString(), toCurrency_code.getText().toString(), noOfDays);
                Toast.makeText(getContext(), "month view", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void switchCurrencys() {
        String tempCode = fromCurrency_code.getText().toString();
        fromCurrency_code.setText(toCurrency_code.getText().toString());
        toCurrency_code.setText(tempCode);
        fetchHistoricalData(fromCurrency_code.getText().toString(), toCurrency_code.getText().toString(), noOfDays);
        // Swap the flag images using Picasso
        Drawable tempFlag = flagFromCurrency.getDrawable();
        flagFromCurrency.setImageDrawable(flagToCurrency.getDrawable());
        flagToCurrency.setImageDrawable(tempFlag);

    }

    private void fetchHistoricalData(String baseCurrency, String targetCurrency, int days) {
        Calendar calendar = Calendar.getInstance();
        entries.clear();
        for (int i = 0; i < days; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            String date = String.format("%tF", calendar);

            Log.d(TAG, "Fetching data for date: " + date);

            Call<ExchangeRatesResponse> call = apiService.getHistoricalRates(date, API_KEY);
            final int index = days - i;

            call.enqueue(new Callback<ExchangeRatesResponse>() {
                @Override
                public void onResponse(@NonNull Call<ExchangeRatesResponse> call, @NonNull Response<ExchangeRatesResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Double> rates = response.body().getRates();
                        if (rates.containsKey(baseCurrency) && rates.containsKey(targetCurrency)) {
                            double fromRate = rates.get(baseCurrency);
                            double toRate = rates.get(targetCurrency);
                            double convertedRate = toRate / fromRate;
                            entries.add(new Entry(index, (float) convertedRate));

                            Log.d(TAG, "Added entry: " + new Entry(index, (float) convertedRate));

                            if (entries.size() == days) {
                                plotChart();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ExchangeRatesResponse> call, @NonNull Throwable t) {
                    // Handle failure
                    Log.e(TAG, "Failed to fetch historical rates", t);
                }
            });
        }
    }

    private void plotChart() {
        Log.d(TAG, "Plotting chart with " + entries.size() + " point values.");

        entries.sort(new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                return Float.compare(o1.getX(), o2.getX());
            }
        });

        LineDataSet dataSet = new LineDataSet(entries, "Exchange Rate");

        dataSet.setDrawValues(false);
        dataSet.setColor(Color.parseColor("#FF4081"));
        dataSet.setLineWidth(2.5f); // Set the width of the line
        dataSet.setCircleColor(Color.parseColor("#FF4081"));
        dataSet.setCircleRadius(4f); // Set the radius of the circles
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#FF4081"));
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Position the X-axis at the bottom
        xAxis.setDrawGridLines(false); // Disable grid lines on the X-axis
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setDrawGridLines(false); // Disable grid lines on the Y-axis
        lineChart.animateX(1000);
        lineChart.invalidate();
    }
}

