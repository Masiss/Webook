package com.example.webook.utils;

public class UserS {
    private String userName;
    private String id;
    private int balance;
    private String email;

    public UserS(String userName, int balance, String email, String id) {
        this.userName = userName;
        this.balance = balance;
        this.email = email;
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public int getBalance() {
        return balance;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }
}
