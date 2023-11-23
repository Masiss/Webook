package com.example.webook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.webook.databinding.ActivityListChapterBinding;

public class ListChapterActivity extends AppCompatActivity {
    ActivityListChapterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListChapterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}