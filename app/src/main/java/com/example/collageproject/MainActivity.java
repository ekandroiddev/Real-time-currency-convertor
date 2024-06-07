package com.example.collageproject;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.collageproject.fragments.ConversionFragment;
import com.example.collageproject.fragments.StatsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    String code, flagURL, nature, fragmentToOpen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavBar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#1f88fc"));


        Intent intent = getIntent();
        fragmentToOpen = intent.getStringExtra("openFragment");
        code = intent.getStringExtra("code");
        flagURL = intent.getStringExtra("flagURL");
        nature = intent.getStringExtra("nature");

        Log.d("MainActivity", "fragmentToOpen: " + fragmentToOpen);
        Log.d("MainActivity", "code: " + code);
        Log.d("MainActivity", "flagURL: " + flagURL);
        Log.d("MainActivity", "nature: " + nature);

        Bundle bundle = new Bundle();
        bundle.putString("code", code);
        bundle.putString("flagURL", flagURL);
        bundle.putString("nature", nature);

        if ("ConversionFragment".equals(fragmentToOpen)) {
            ConversionFragment conversionFragment = new ConversionFragment();
            conversionFragment.setArguments(bundle);
            loadFragment(conversionFragment);
        } else if ("StatsFragment".equals(fragmentToOpen)) {

            StatsFragment statsFragment = new StatsFragment();
            statsFragment.setArguments(bundle);
            loadFragment(statsFragment);
        } else {
            ConversionFragment conversionFragment = new ConversionFragment();
            conversionFragment.setArguments(bundle);
            loadFragment(conversionFragment);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                ConversionFragment fragment = new ConversionFragment();
                fragment.setArguments(bundle);
                loadFragment(fragment);
            } else {
                StatsFragment statsFragment = new StatsFragment();
                statsFragment.setArguments(bundle);
                loadFragment(statsFragment);
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    public void loadFragment(Fragment fragment) {
        loadFragment(fragment, null);
    }

    public void loadFragment(Fragment fragment, Bundle bundle) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentsContainer, fragment);
        fragmentTransaction.commit();
    }
}