package com.example.webook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.webook.databinding.ActivityReadingBooksBinding;

public class ReadingBooksActivity extends AppCompatActivity {
    ActivityReadingBooksBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReadingBooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}