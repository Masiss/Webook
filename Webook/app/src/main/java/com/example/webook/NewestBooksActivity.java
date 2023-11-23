package com.example.webook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.webook.databinding.ActivityNewestBooksBinding;

public class NewestBooksActivity extends AppCompatActivity {

    ActivityNewestBooksBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewestBooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}