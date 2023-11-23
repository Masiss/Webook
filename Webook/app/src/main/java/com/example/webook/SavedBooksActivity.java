package com.example.webook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.webook.databinding.ActivitySavedBooksBinding;

public class SavedBooksActivity extends AppCompatActivity {
    ActivitySavedBooksBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedBooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}