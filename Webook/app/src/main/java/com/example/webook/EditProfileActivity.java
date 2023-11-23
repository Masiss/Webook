package com.example.webook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.webook.databinding.ActivityEditProfileBinding;
import com.example.webook.utils.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class EditProfileActivity extends AppCompatActivity {

    EditText username, phone, email;
    TextView btnSubmit;
    ActivityEditProfileBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SessionManager sessionManager = new SessionManager(this);
        username = findViewById(R.id.username);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        btnSubmit = findViewById(R.id.btn_submit);


        db.collection("user").document(sessionManager.getUserId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        username.setText(documentSnapshot.getString("username"));
                        email.setText(documentSnapshot.getString("email"));
                        phone.setText(documentSnapshot.getString("phone"));
                    }
                });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!email.getText().toString().equals(sessionManager.getUserDetails().getEmail())) {

                    db.collection("user")
                            .whereEqualTo("email", email.getText().toString().trim())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().size() == 0) {
                                            db.collection("user")
                                                    .document(sessionManager.getUserId())
                                                    .update("username", username.getText().toString(),
                                                            "email", email.getText().toString(),
                                                            "phone", phone.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(getApplicationContext(), "Cập nhật thông tin thành công", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(getApplicationContext(), "email đã tồn tại, vui lòng thử lại email khác", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                }else{
                    db.collection("user")
                            .document(sessionManager.getUserId())
                            .update("username", username.getText().toString(),
                                    "phone", phone.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), "Cập nhật thông tin thành công", Toast.LENGTH_LONG).show();
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