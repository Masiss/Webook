package com.example.webook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.webook.databinding.ActivitySubscriptionBinding;

public class SubscriptionActivity extends AppCompatActivity {
    ActivitySubscriptionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}