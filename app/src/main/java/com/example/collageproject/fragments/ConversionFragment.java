package com.example.collageproject.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.collageproject.AllCurrencyViewActivity;
import com.example.collageproject.ExchangeRatesApi;
import com.example.collageproject.R;
import com.example.collageproject.RetrofitClient;
import com.example.collageproject.models.ExchangeRatesResponse;
import com.example.collageproject.utils.NetworkUtil;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversionFragment extends Fragment {
    EditText editTextFromAmount, editTextToAmount;
    ImageView switchBtn, fromCurrencyFlag, toCurrencyFlag;
    TextView fromCurrencyCode, toCurrencyCode;
    private StringBuilder input;
    LinearLayout fromCurrency, toCurrency;
    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0, btnpoint, btnbackSpace;
    Intent intent;
    ExchangeRatesApi apiService;

    private static final String API_KEY = "2b16efb060b6413eb2c500f5717f7cff";

    public ConversionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = RetrofitClient.getRetrofitInstance().create(ExchangeRatesApi.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversion, container, false);
        editTextFromAmount = view.findViewById(R.id.from_currency_amount);
        editTextToAmount = view.findViewById(R.id.to_currency_amount);
        fromCurrency = view.findViewById(R.id.from_currency);
        toCurrency = view.findViewById(R.id.to_currency);
        switchBtn = view.findViewById(R.id.switchBtn);
        fromCurrencyFlag = view.findViewById(R.id.fromCurrencyflag);
        fromCurrencyCode = view.findViewById(R.id.fromCurrencyCode);
        toCurrencyFlag = view.findViewById(R.id.toCurrencyFlag);
        toCurrencyCode = view.findViewById(R.id.toCurrencyCode);
        input = new StringBuilder();
        disableSystemKeyboard(editTextFromAmount);

        btn0 = view.findViewById(R.id.btn_0);
        btn1 = view.findViewById(R.id.btn_1);
        btn2 = view.findViewById(R.id.btn_2);
        btn3 = view.findViewById(R.id.btn_3);
        btn4 = view.findViewById(R.id.btn_4);
        btn5 = view.findViewById(R.id.btn_5);
        btn6 = view.findViewById(R.id.btn_6);
        btn7 = view.findViewById(R.id.btn_7);
        btn8 = view.findViewById(R.id.btn_8);
        btn9 = view.findViewById(R.id.btn_9);
        btnpoint = view.findViewById(R.id.btn_point);
        btnbackSpace = view.findViewById(R.id.btn_clear);

        if (NetworkUtil.isNetworkAvailable(requireContext())) {
            Toast.makeText(getContext(), "Internet is available", Toast.LENGTH_SHORT).show();
        } else {
            showNoInternetDialog();
            Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCurrencies();
            }
        });

        editTextFromAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                if (!input.isEmpty()) {
                    if (!input.matches("\\d*\\.?\\d*")) {
                        Toast.makeText(getContext(), "Only numbers are allowed", Toast.LENGTH_SHORT).show();
                    } else {
                        double amount = Double.parseDouble(s.toString());
                        performCurrencyConversion(amount);
                    }
                } else {
                    editTextToAmount.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btn0.setOnClickListener(numberPadClickListener);
        btn1.setOnClickListener(numberPadClickListener);
        btn2.setOnClickListener(numberPadClickListener);
        btn3.setOnClickListener(numberPadClickListener);
        btn4.setOnClickListener(numberPadClickListener);
        btn5.setOnClickListener(numberPadClickListener);
        btn6.setOnClickListener(numberPadClickListener);
        btn7.setOnClickListener(numberPadClickListener);
        btn8.setOnClickListener(numberPadClickListener);
        btn9.setOnClickListener(numberPadClickListener);
        btnpoint.setOnClickListener(numberPadClickListener);
        btnbackSpace.setOnClickListener(numberPadClickListener);

        updateUIFromArguments(getArguments());

        fromCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getContext(), AllCurrencyViewActivity.class);
                intent.putExtra("currencyNature", "fromCurrency");
                startActivity(intent);
            }
        });
        toCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getContext(), AllCurrencyViewActivity.class);
                intent.putExtra("currencyNature", "toCurrency");
                startActivity(intent);
            }
        });

        return view;
    }

    private void showNoInternetDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("No Internet Connection")
                .setMessage("The app requires an internet connection to proceed. Please connect to the internet and try again.")
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.finishAffinity(requireActivity());
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void updateUIFromArguments(Bundle arguments) {
        if (arguments != null) {
            String code = arguments.getString("code");
            String flagUrl = arguments.getString("flagURL");
            String nature = arguments.getString("nature");

            Log.d("ConversionFragment", "code: " + code);
            Log.d("ConversionFragment", "flagURL: " + flagUrl);
            Log.d("ConversionFragment", "nature: " + nature);

            int defaultFlagResource = R.drawable.deffaultflag;

            if ("fromCurrency".equals(nature)) {
                Log.d("ConversionFragment", "Updating fromCurrency UI components");
                fromCurrencyCode.setText(code);
                if (flagUrl != null && !flagUrl.isEmpty()) {
                    Picasso.get().load(flagUrl).into(fromCurrencyFlag);
                } else {
                    fromCurrencyFlag.setBackgroundResource(defaultFlagResource);
                }

                // Log values after setting them
                Log.d("ConversionFragment", "fromCurrencyCode text: " + fromCurrencyCode.getText().toString());
            } else if ("toCurrency".equals(nature)) {
                Log.d("ConversionFragment", "Updating toCurrency UI components");
                toCurrencyCode.setText(code);
                if (flagUrl != null && !flagUrl.isEmpty()) {
                    Picasso.get().load(flagUrl).into(toCurrencyFlag);
                } else {
                    toCurrencyFlag.setBackgroundResource(defaultFlagResource);
                }

                // Log values after setting them
                Log.d("ConversionFragment", "toCurrencyCode text: " + toCurrencyCode.getText().toString());
            }
            // Verify if the views are visible and updated
            Log.d("ConversionFragment", "fromCurrencyCode visibility: " + fromCurrencyCode.getVisibility());
            Log.d("ConversionFragment", "fromCurrencyFlag visibility: " + fromCurrencyFlag.getVisibility());
            Log.d("ConversionFragment", "toCurrencyCode visibility: " + toCurrencyCode.getVisibility());
            Log.d("ConversionFragment", "toCurrencyFlag visibility: " + toCurrencyFlag.getVisibility());

        }
    }

    private void switchCurrencies() {
        // Swap the currency codes
        String tempCode = fromCurrencyCode.getText().toString();
        fromCurrencyCode.setText(toCurrencyCode.getText().toString());
        toCurrencyCode.setText(tempCode);

        // Swap the flag images using Picasso
        Drawable tempFlag = fromCurrencyFlag.getDrawable();
        fromCurrencyFlag.setImageDrawable(toCurrencyFlag.getDrawable());
        toCurrencyFlag.setImageDrawable(tempFlag);

        // Trigger conversion with updated currencies
        String fromAmountStr = editTextFromAmount.getText().toString();
        if (!fromAmountStr.isEmpty()) {
            double fromAmount = Double.parseDouble(fromAmountStr);
            performCurrencyConversion(fromAmount);
        }
    }

    private void performCurrencyConversion(double amount) {
        String fromCode = fromCurrencyCode.getText().toString();
        String toCode = toCurrencyCode.getText().toString();

        Call<ExchangeRatesResponse> call = apiService.getLatestRates(API_KEY);
        call.enqueue(new Callback<ExchangeRatesResponse>() {
            @Override
            public void onResponse(@NonNull Call<ExchangeRatesResponse> call, @NonNull Response<ExchangeRatesResponse> response) {
                if (response.isSuccessful()) {
                    ExchangeRatesResponse ratesResponse = response.body();
                    if (ratesResponse != null) {
                        Map<String, Double> rates = ratesResponse.getRates();
                        Double fromRate = rates.get(fromCode);
                        Double toRate = rates.get(toCode);
                        if (fromRate != null && toRate != null) {
                            double convertedAmount = (amount / fromRate) * toRate;
                            DecimalFormat df = new DecimalFormat("#.##"); // Format to two decimal places
                            String formattedAmount = df.format(convertedAmount);
                            editTextToAmount.setText(formattedAmount);
                        } else {
                            // Handle missing rate
                            Toast.makeText(getContext(), "Rate not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Handle error
                    Toast.makeText(getContext(), "Failed to fetch rates", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExchangeRatesResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Request failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void disableSystemKeyboard(EditText editText) {
        editText.setShowSoftInputOnFocus(false);
    }

    private final View.OnClickListener numberPadClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            String buttonText = button.getText().toString();

            switch (buttonText) {
                case "⌫":
                    if (input.length() > 0) {
                        input.deleteCharAt(input.length() - 1);
                    }
                    break;
                case ".":
                    if (!input.toString().contains(".")) {
                        if (input.length() == 0) {
                            input.append("0.");
                        } else {
                            input.append(buttonText);
                        }
                    }
                    break;
                default:
                    input.append(buttonText);
                    break;
            }
            editTextFromAmount.setText(input.toString());
            editTextFromAmount.setSelection(input.length());
        }
    };
}
