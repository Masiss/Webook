package com.example.webook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.webook.databinding.ActivityDoiMatKhauBinding;
import com.example.webook.utils.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DoiMatKhauActivity extends AppCompatActivity {
    EditText oldPassword, newPassword, passwordRepeat;
    ActivityDoiMatKhauBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoiMatKhauBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SessionManager sessionManager = new SessionManager(this);

        oldPassword = findViewById(R.id.edt_old_password);
        newPassword = findViewById(R.id.edt_new_password);
        passwordRepeat = findViewById(R.id.edt_repeatPassword);


        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oldPassword.getText() != newPassword.getText() && newPassword.getText() == passwordRepeat.getText()) {
                    db.collection("user")
                            .document(sessionManager.getUserId())
                            .update("password", newPassword.getText())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Cập nhật mật khẩu thành công", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Cập nhật thất bại, vui lòng xem lại thông tin đã tiền", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });


        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}