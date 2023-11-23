package com.example.webook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.webook.ListBookActivity;
import com.example.webook.R;
import com.example.webook.adapter.VerticalBookAdapter;
import com.example.webook.model.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText searchBar;
    private List<Book> bookList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Book> suggestBook;
    private RecyclerView suggestRv;
    private VerticalBookAdapter adapter;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchBar = view.findViewById(R.id.searchBar);

        suggestBook = new ArrayList<Book>();
        suggestRv = view.findViewById(R.id.suggestRv);
        suggestRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new VerticalBookAdapter(getContext(), suggestBook);
        suggestRv.setAdapter(adapter);
        db.collection("book")
                .orderBy("sold", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            Book book = new Book();
                            book.setId(documentSnapshot.getId());
                            book.setTitle(documentSnapshot.getString("title"));
                            book.setImage(documentSnapshot.getString("image"));
                            book.setPrice(documentSnapshot.getLong("price").intValue());
                            book.setDescription(documentSnapshot.getString("description"));
                            book.setChapter((List<String>) documentSnapshot.get("chapter"));
                            book.setContent(documentSnapshot.getString("content"));
                            book.setAuthor(documentSnapshot.getString("author"));
                            suggestBook.add(book);
                        }

                        adapter.notifyDataSetChanged();
                    }
                });


        searchBar.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    String input = searchBar.getText().toString();
                    if (input.isEmpty()) {
                        Toast.makeText(getContext(), "Bạn chưa nhập gì cả", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(getContext(), ListBookActivity.class);
                        intent.putExtra("input", input);
                        startActivity(intent);

                    }
                    Log.d("aaaa", input);
                    //do something here
                    return true;

                }
                return false;

            }
        });
    }
}