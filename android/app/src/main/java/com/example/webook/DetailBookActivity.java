package com.example.webook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.webook.adapter.ChapterAdapter;
import com.example.webook.model.Book;
import com.example.webook.utils.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webook.databinding.ActivityDetailBookBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetailBookActivity extends AppCompatActivity {

    private ActivityDetailBookBinding binding;
    private ImageView imageView;
    private TextView btnReadNow;
    private TextView title, author, description, price;
    private RecyclerView chapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<Map<String, Object>> pLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager sessionManager = new SessionManager(this);
        binding = ActivityDetailBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Book book = (Book) getIntent().getSerializableExtra("book");
        imageView = findViewById(R.id.book_image);
        title = findViewById(R.id.title);
        author = findViewById(R.id.author);
        description = findViewById(R.id.description);
        chapter = findViewById(R.id.chapters);
        price = findViewById(R.id.price);
        btnReadNow = findViewById(R.id.btn_read_now);

        db.collection("user")
                .document(sessionManager.getUserId())
                .collection("purchase_log")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean exists = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                String bookName = data.get("book").toString();
                                if (bookName.equals(book.getId())) {
                                    exists = true;
                                    btnReadNow.setText("ĐỌC NGAY");
                                    binding.btnReadNow.setOnClickListener(v -> {
                                        Intent intent = new Intent(getApplicationContext(), ChapterActivity.class);
                                        intent.putExtra("content", book.getContent());
                                        startActivity(intent);
                                    });
                                }
                            }
                            if (!exists) {
                                btnReadNow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        int balance = sessionManager.getUserDetails().getBalance();
                                        if (balance >= book.getPrice()) {
                                            db.collection("book").document(book.getId())
                                                    .update("sold", book.getSold() + 1);
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("book", book.getId());
                                            data.put("time", FieldValue.serverTimestamp());
                                            db.collection("user").document(sessionManager.getUserId())
                                                    .update("balance", sessionManager.getUserDetails().getBalance() - book.getPrice());
                                            db.collection("user").document(sessionManager.getUserId())
                                                    .collection("purchase_log")
                                                    .add(data)
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(getApplicationContext(), "Mua sách thành công", Toast.LENGTH_LONG).show();
                                                                startActivity(getIntent());
                                                                finish();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Số dư không đủ, hãy nạp tiền", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }

                        } else {
                            Log.d("aaaaa", "Error getting documents: ", task.getException());
                        }
                    }
                });
        title.setText(book.getTitle());
        author.setText("Tác giả: " + book.getAuthor());
        description.setText(book.getDescription());
        price.setText("Giá " + String.valueOf(NumberFormat.getNumberInstance(new Locale("vi", "VN")).format(book.getPrice())) + "vnđ");

        ChapterAdapter chapterAdapter = new ChapterAdapter(this, book.getChapter());
        chapter.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        chapter.hasFixedSize();
        chapter.setAdapter(chapterAdapter);

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(book.getImage());
        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(getApplicationContext())
                            .load(task.getResult())
                            .into(imageView);
                }

            }
        });


        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }

}