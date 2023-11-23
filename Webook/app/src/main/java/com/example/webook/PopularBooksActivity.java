package com.example.webook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.webook.databinding.ActivityPopularBooksBinding;

public class PopularBooksActivity extends AppCompatActivity {
    ActivityPopularBooksBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPopularBooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}