package com.example.webook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.webook.databinding.ActivitySignUpBinding;
import com.example.webook.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private EditText email, phone, userName, password, passwordRepeat;
    private Button btnSignUp;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        email = findViewById(R.id.edt_email);
        phone = findViewById(R.id.edt_phone);
        userName = findViewById(R.id.edt_username);
        password = findViewById(R.id.edt_password);
        passwordRepeat = findViewById(R.id.edt_confirmPass);

        btnSignUp = findViewById(R.id.btn_sign_up);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("user")
                        .whereEqualTo("email", email.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (password.getText().toString() != passwordRepeat.getText().toString() && task.getResult().size() > 0) {
                                    Toast.makeText(getApplicationContext(), "Email dã tồn tại hoặc mật khẩu trùng", Toast.LENGTH_LONG).show();
                                } else {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("username", userName.getText().toString());
                                    data.put("email", email.getText().toString());
                                    data.put("password", password.getText().toString());
                                    data.put("phone", phone.getText().toString());
                                    data.put("balance", 0);
                                    data.put("purchase_log", new ArrayList<>());
                                    db.collection("user")
                                            .add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(SignUpActivity.this, "Đăng ký thất bại", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });


            }
        });
        SpannableString span = new SpannableString(binding.txtSignIn.getText());
        span.setSpan(new UnderlineSpan(), binding.txtSignIn.getText().length() - "Đăng nhập".length(), binding.txtSignIn.getText().length(), 0);
        binding.txtSignIn.setText(span);

        binding.txtSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        });
    }
}