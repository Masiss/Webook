package com.example.webook.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.webook.ListBookActivity;
import com.example.webook.R;
import com.example.webook.adapter.HorizontalBookAdapter;
import com.example.webook.adapter.VerticalBookAdapter;
import com.example.webook.databinding.FragmentHomeBinding;
import com.example.webook.model.Author;
import com.example.webook.model.Book;
import com.example.webook.utils.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    private ImageView btnNewest, btnPopular;
    private LinearLayout btnBookDetail;
    private RecyclerView firstRecycler, freeRv, readingRv, popularRv;
    private ArrayList<Book> books;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private HorizontalBookAdapter horizontalBookAdapter, readingBookAdapter, popularAdapter;
    private VerticalBookAdapter verticalBookAdapter;
    private ArrayList<Author> authors;
    private ArrayList<Book> freeBook;
    private ArrayList<Book> readingBook;
    private ArrayList<Book> popularBook;
    private SessionManager sessionManager;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {

            super.onViewCreated(view, savedInstanceState);

            sessionManager = new SessionManager(this.getContext());
            sessionManager.checkLogin();
            db.collection("user")
                    .document(sessionManager.getUserId())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            sessionManager.createLoginSession(documentSnapshot.getString("username"),
                                    documentSnapshot.getString("email"),
                                    documentSnapshot.getLong("balance").intValue(),
                                    documentSnapshot.getId());
                        }
                    });
            books = new ArrayList<Book>();
            authors = new ArrayList<Author>();
            freeBook = new ArrayList<Book>();
            readingBook = new ArrayList<Book>();
            popularBook = new ArrayList<Book>();

            firstRecycler = view.findViewById(R.id.firstRecycler);
            firstRecycler.setHasFixedSize(true);
            firstRecycler.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
            horizontalBookAdapter = new HorizontalBookAdapter(getContext(), books);
            firstRecycler.setAdapter(horizontalBookAdapter);


            readingRv = view.findViewById(R.id.readingRv);
            readingRv.setHasFixedSize(true);
            readingRv.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));


            freeRv = view.findViewById(R.id.freeRv);
            freeRv.setHasFixedSize(true);
            freeRv.setLayoutManager(new LinearLayoutManager(view.getContext()));

            popularRv = view.findViewById(R.id.popularRv);
            popularRv.setHasFixedSize(true);
            popularRv.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));


            getBooks();

            binding.btnPopularBook.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ListBookActivity.class);
                intent.putExtra("input", "popular");
                startActivity(intent);
            });

            binding.btnFreeBook.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ListBookActivity.class);
                intent.putExtra("input", "free");
                startActivity(intent);
            });
            binding.btnNewestBook.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ListBookActivity.class);
                intent.putExtra("input", "newest");
                startActivity(intent);
            });
            binding.btnReadingBook.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ListBookActivity.class);
                intent.putExtra("input", "reading");
                startActivity(intent);
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Mạng yếu", Toast.LENGTH_LONG).show();
        }

    }

    ArrayList<Map<String, Object>> pLog = new ArrayList<>();


    public void getPurchaseLog() {
        pLog.clear();
        db.collection("user")
                .document(sessionManager.getUserId())
                .collection("purchase_log")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Map<String, Object> data = documentSnapshot.getData();
                                pLog.add(data);
                            }
                            for (Map<String, Object> id : pLog.size() < 5 ? pLog : pLog.subList(0, 5)) {
                                db.collection("book")
                                        .document(id.get("book").toString())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                Book book = new Book();
                                                book.setId(documentSnapshot.getId());
                                                book.setTitle(documentSnapshot.getString("title"));
                                                book.setImage(documentSnapshot.getString("image"));
                                                book.setPrice(documentSnapshot.getLong("price").intValue());
                                                book.setDescription(documentSnapshot.getString("description"));
                                                book.setChapter((List<String>) documentSnapshot.get("chapter"));
                                                book.setContent(documentSnapshot.getString("content"));
                                                book.setAuthor(documentSnapshot.getString("author"));
                                                readingBook.add(book);
                                                readingBookAdapter.notifyDataSetChanged();
                                            }

                                        });
                            }
                        }
                    }
                });
    }

    public void getBooks() {
        db.collection("book")
                .whereEqualTo("is_published", true)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Book book = new Book();
                                book.setId(document.getId());
                                book.setTitle(document.getString("title"));
                                book.setImage(document.getString("image"));
                                book.setPrice(document.getLong("price").intValue());
                                book.setDescription(document.getString("description"));
                                book.setChapter((List<String>) document.get("chapter"));
                                book.setContent(document.getString("content"));
                                book.setAuthor(document.getString("author"));
                                book.setSold(document.getLong("sold").intValue());
                                books.add(book);
                            }

                            horizontalBookAdapter.notifyDataSetChanged();
                            freeBook = (ArrayList<Book>) books.stream().filter(c -> c.getPrice() == 0).limit(5).collect(Collectors.toList());

                            verticalBookAdapter = new VerticalBookAdapter(getContext(), freeBook);
                            freeRv.setAdapter(verticalBookAdapter);
                            verticalBookAdapter.notifyDataSetChanged();


                            getPurchaseLog();
//                            readingBook = (ArrayList<Book>) books.stream().filter(c -> pLog.contains(c.getId())).collect(Collectors.toList());
                            readingBookAdapter = new HorizontalBookAdapter(getContext(), readingBook);
                            readingRv.setAdapter(readingBookAdapter);
                            readingBookAdapter.notifyDataSetChanged();

                            books.sort(Comparator.comparing(Book::getSold).reversed());
                            popularBook = books;
                            popularAdapter = new HorizontalBookAdapter(getContext(), popularBook);
                            popularRv.setAdapter(popularAdapter);
                            popularAdapter.notifyDataSetChanged();

                        }
                    }

                });

    }


}