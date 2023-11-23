package com.example.webook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.webook.adapter.VerticalBookAdapter;
import com.example.webook.databinding.ActivityListBookBinding;
import com.example.webook.model.Author;
import com.example.webook.model.Book;
import com.example.webook.utils.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ListBookActivity extends AppCompatActivity {
    ActivityListBookBinding binding;
    RecyclerView recyclerView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Book> books;
    ArrayList<Author> authors;
    VerticalBookAdapter verticalBookAdapter;
    private SessionManager sessionManager;
    TextView activityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(this);
        books = new ArrayList<Book>();
        authors = new ArrayList<Author>();
        Intent intent = getIntent();
        activityTitle = findViewById(R.id.activityTitle);
        String input = intent.getStringExtra("input");
        recyclerView = findViewById(R.id.verticalRv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        verticalBookAdapter = new VerticalBookAdapter(getApplicationContext(), books);
        getBooks(input);
        recyclerView.setAdapter(verticalBookAdapter);


        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    public void getBooks(String input) {
        String[] arr = {"free", "newest", "reading", "popular"};
        if (!Arrays.asList(arr).contains(input)) {
            input = input.toLowerCase(Locale.ROOT);
            String finalInput = input;
            db.collection("book")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                if (documentSnapshot.getString("title").contains(finalInput)) {

                                    Book book = new Book();
                                    book.setImage(documentSnapshot.getString("image"));
                                    book.setChapter((List<String>) documentSnapshot.get("chapter"));
                                    book.setId(documentSnapshot.getId());
                                    book.setDescription(documentSnapshot.getString("description"));
                                    book.setAuthor(documentSnapshot.getString("author"));
                                    book.setTitle(documentSnapshot.getString("title"));
                                    book.setPrice(documentSnapshot.getLong("price").intValue());
                                    books.add(book);
                                }
                            }

                            verticalBookAdapter.notifyDataSetChanged();
                        }
                    });
        } else {
            switch (input) {
                case "free":
                    activityTitle.setText("SÁCH MIỄN PHÍ");
                    db.collection("book")
                            .whereEqualTo("price", 0)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                        Book book = new Book();
                                        book.setImage(documentSnapshot.getString("image"));
                                        book.setChapter((List<String>) documentSnapshot.get("chapter"));
                                        book.setId(documentSnapshot.getId());
                                        book.setDescription(documentSnapshot.getString("description"));
                                        book.setAuthor(documentSnapshot.getString("author"));
                                        book.setTitle(documentSnapshot.getString("title"));
                                        book.setPrice(documentSnapshot.getLong("price").intValue());
                                        books.add(book);
                                    }
                                    verticalBookAdapter.notifyDataSetChanged();

                                }
                            });
                    break;
                case "newest":
                    activityTitle.setText("SÁCH MỚI NHẤT");

                    db.collection("book")
                            .orderBy("created_at", Query.Direction.DESCENDING)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                        Book book = new Book();
                                        book.setImage(documentSnapshot.getString("image"));
                                        book.setChapter((List<String>) documentSnapshot.get("chapter"));
                                        book.setId(documentSnapshot.getId());
                                        book.setDescription(documentSnapshot.getString("description"));
                                        book.setAuthor(documentSnapshot.getString("author"));
                                        book.setTitle(documentSnapshot.getString("title"));
                                        book.setPrice(documentSnapshot.getLong("price").intValue());
                                        books.add(book);
                                    }
                                    verticalBookAdapter.notifyDataSetChanged();

                                }
                            });
                    break;
                case "reading":
                    activityTitle.setText("SÁCH ĐANG ĐỌC");
                    db.collection("user")
                            .document(sessionManager.getUserId())
                            .collection("purchase_log")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        String book = documentSnapshot.getString("book");
                                        db.collection("book")
                                                .document(book)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        DocumentSnapshot documentSnapshot = task.getResult();
                                                        Book book = new Book();
                                                        book.setImage(documentSnapshot.getString("image"));
                                                        book.setChapter((List<String>) documentSnapshot.get("chapter"));
                                                        book.setId(documentSnapshot.getId());
                                                        book.setDescription(documentSnapshot.getString("description"));
                                                        book.setAuthor(documentSnapshot.getString("author"));
                                                        book.setTitle(documentSnapshot.getString("title"));
                                                        book.setPrice(documentSnapshot.getLong("price").intValue());
                                                        books.add(book);
                                                        verticalBookAdapter.notifyDataSetChanged();

                                                    }
                                                });
                                        verticalBookAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
//
                    break;
                case "popular":
                    activityTitle.setText("SÁCH PHỔ BIẾN");
                    db.collection("book")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                        Book book = new Book();
                                        book.setImage(documentSnapshot.getString("image"));
                                        book.setChapter((List<String>) documentSnapshot.get("chapter"));
                                        book.setId(documentSnapshot.getId());
                                        book.setDescription(documentSnapshot.getString("description"));
                                        book.setAuthor(documentSnapshot.getString("author"));
                                        book.setTitle(documentSnapshot.getString("title"));
                                        book.setPrice(documentSnapshot.getLong("price").intValue());
                                        book.setSold(documentSnapshot.getLong("sold").intValue());
                                        books.add(book);
                                    }
                                    verticalBookAdapter.notifyDataSetChanged();

                                }
                            });
                    break;
            }

        }

    }
}