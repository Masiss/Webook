package com.example.webook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;
import com.example.webook.databinding.ActivitySignInBinding;
import com.example.webook.model.User;
import com.example.webook.utils.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SignInActivity extends AppCompatActivity {
    private Button btn_signin;
    private TextView signUp, forgetPassBtn;
    private EditText username;
    private EditText password;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        SessionManager sessionManager = new SessionManager(this);
        Log.d("alooooooooo", "1");
        signUp = findViewById(R.id.txt_sign_up);
        username = findViewById(R.id.edt_username);
        password = findViewById(R.id.edt_password);
        btn_signin = findViewById(R.id.btn_sign_in);
        forgetPassBtn = findViewById(R.id.forgetPassBtn);
        forgetPassBtn.setOnClickListener(v->{
            Intent intent= new Intent(this,ForgetPasswordActivity.class);
            startActivity(intent);
        });
        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
btn_signin.setOnClickListener(v -> {
    String userName = username.getText().toString().trim();
    String pass = password.getText().toString().trim();

    if (userName.isEmpty() || pass.isEmpty()) {
        Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
        return;
    }

    db.collection("user")
        .whereEqualTo("username", userName)
        .whereEqualTo("password", pass)
        .limit(1)
        .get()
        .addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                // THÀNH CÔNG: Lấy document đầu tiên
                DocumentSnapshot document = task.getResult().getDocuments().get(0);

                // Lưu session
                sessionManager.createLoginSession(
                    document.getString("username"),
                    document.getString("email"),
                    document.getLong("balance").intValue(),
                    document.getId()
                );

                Log.d("LOGIN", "Đăng nhập thành công! UID: " + document.getId());

                // CHUYỂN ACTIVITY
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Đóng SignInActivity

            } else {
                // THẤT BẠI
                Exception e = task.getException();
                Log.e("LOGIN", "Đăng nhập thất bại", e);
                Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_LONG).show();
            }
        });
});
            }


}
