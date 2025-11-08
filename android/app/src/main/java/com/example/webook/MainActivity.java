package com.example.webook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.webook.databinding.ActivityMainBinding;
import com.example.webook.fragment.HomeFragment;
import com.example.webook.fragment.SearchFragment;
import com.example.webook.fragment.SettingFragment;
import com.example.webook.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView
        .OnNavigationItemSelectedListener {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SessionManager sessionManager = new SessionManager(this.getApplicationContext());
        sessionManager.checkLogin();
        binding.bottomNavbar.setOnNavigationItemSelectedListener(this);
        binding.bottomNavbar.setSelectedItemId(R.id.item_home);

    }

    HomeFragment homeFragment = new HomeFragment();
    SettingFragment settingFragment = new SettingFragment();

    SearchFragment searchFragment = new SearchFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_home) {
            getSupportFragmentManager().beginTransaction().replace(binding.container.getId(), homeFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.item_person) {
            getSupportFragmentManager().beginTransaction().replace(binding.container.getId(), settingFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.item_search) {
            getSupportFragmentManager().beginTransaction().replace(binding.container.getId(), searchFragment).commit();
            return true;
        }

        return false;
    }
}
