package com.example.webook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;

public class ForgetPasswordActivity extends AppCompatActivity {
    EditText username, email;
    TextView tvSignIn;
    Button btnSubmit;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        username = findViewById(R.id.edt_username);
        email = findViewById(R.id.edt_email);
        tvSignIn = findViewById(R.id.tv_sign_in);
        btnSubmit = findViewById(R.id.btn_submit);
        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = username.getText().toString();
                String Email = email.getText().toString();
                if (!userName.isEmpty() || !Email.isEmpty()) {
                    db.collection("user").whereEqualTo("username", userName).whereEqualTo("email", Email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() > 0 && task.getResult().size() < 2) {
                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                        HashMap<String, Object> forgetPass = new HashMap<>();
                                        forgetPass.put("username", userName);
                                        forgetPass.put("email", Email);
                                        forgetPass.put("created_at", new Timestamp(new Date()));
                                        db.collection("forget_pass").add(forgetPass).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                Toast.makeText(getApplicationContext(), "Gửi yêu cầu thành công", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });

    }
}