package com.example.webook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.webook.databinding.ActivitySignInBinding;
import com.example.webook.model.User;
import com.example.webook.utils.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
            String userName = username.getText().toString();
            String pass = password.getText().toString();
            if (!userName.isEmpty() && !pass.isEmpty()) {
                db.collection("user")
                        .whereEqualTo("username", userName)
                        .whereEqualTo("password", pass)
                        .limit(1)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        sessionManager.createLoginSession(document.getString("username"),
                                                document.getString("email"),
                                                document.getLong("balance").intValue(),
                                                document.getId());
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                } else {
                                    Log.d("Error", "Không truy vấn được dữ liệu");
                                }
                            }
                        });
            }

        });
    }


}