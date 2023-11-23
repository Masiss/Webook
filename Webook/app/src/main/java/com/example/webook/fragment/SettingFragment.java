package com.example.webook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.webook.DoiMatKhauActivity;
import com.example.webook.EditProfileActivity;
import com.example.webook.R;
import com.example.webook.RechargeActivity;
import com.example.webook.databinding.FragmentSettingBinding;
import com.example.webook.utils.SessionManager;

import java.text.NumberFormat;
import java.util.Locale;

public class SettingFragment extends Fragment {

    FragmentSettingBinding binding;
    TextView signout;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SessionManager sessionManager = new SessionManager(this.getContext());
        int balance = sessionManager.getUserDetails().getBalance();
        TextView tvBalance = view.findViewById(R.id.balance);
        tvBalance.setText("Số dư: " + NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(balance));
        binding.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivity(intent);
        });
        binding.btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DoiMatKhauActivity.class);
            startActivity(intent);
        });
        binding.btnRecharge.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), RechargeActivity.class);
            startActivity(intent);
        });
        binding.signout.setOnClickListener(v -> {
            sessionManager.logoutUser();
        });
    }
}