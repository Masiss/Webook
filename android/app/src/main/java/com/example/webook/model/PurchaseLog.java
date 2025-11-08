package com.example.webook.model;

import com.google.firebase.Timestamp;

public class PurchaseLog {
    String book;
    Timestamp time;

    public PurchaseLog(String book, Timestamp time) {
        this.book = book;
        this.time = time;
    }

    public PurchaseLog() {

    }

    public String getBook() {
        return this.book;
    }

    public Timestamp getTime() {
        return this.time;
    }
}
