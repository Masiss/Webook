package com.example.webook.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.webook.adapter.HorizontalBookAdapter;
import com.example.webook.model.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Database() {

    }

    public ArrayList<Book> getBook() {
        ArrayList<Book> books = new ArrayList<Book>();
        db.collection("book").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Book book = new Book();
                        book.setId(document.getId());
                        book.setTitle(document.getString("title"));
                        book.setImage(document.getString("image"));
                        book.setPrice(document.getLong("price").intValue());
                        book.setAuthor(getAuthor(document.getString("author")));
                        book.setDescription(document.getString("description"));
                        book.setChapter((List<String>) document.get("chapter"));
                        book.setContent(document.getString("content"));
                        books.add(book);
                    }
                }
            }
        });
        return books;

    }

    public String getAuthor(String author) {
        final String[] returnVal = new String[1];
        String[] arrQuery = author.split("/");
        db.collection("author")
                .document(arrQuery[1])
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        returnVal[0] = task.getResult().getString("name");
                    }
                });
        return returnVal[0];
    }
}
