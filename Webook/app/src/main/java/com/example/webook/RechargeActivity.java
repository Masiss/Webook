package com.example.webook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.webook.utils.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class RechargeActivity extends AppCompatActivity {
    EditText edtPayment, edtMoney;
    TextView btnSubmit;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        edtPayment = findViewById(R.id.edt_payment);
        edtMoney = findViewById(R.id.edt_money);
        btnSubmit = findViewById(R.id.btn_submit);
        SessionManager sessionManager = new SessionManager(this);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> data = new HashMap<>();
                data.put("paymentID", Integer.valueOf(edtPayment.getText().toString()));
                data.put("amount", Integer.valueOf(edtMoney.getText().toString()));
                data.put("userId", sessionManager.getUserId());
                data.put("email", sessionManager.getUserDetails().getEmail());
                db.collection("payment")
                        .add(data)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RechargeActivity.this, "Gửi yêu cầu thành công, email sẽ được gửi khi số tiền đã được cập nhật", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}