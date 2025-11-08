package com.example.webook.model;

import java.util.List;

public class User {
    private String id;
    private String userName;
    private String email;
    private int balance;
    private String password;
    private List<String> purchaseLog;
    public User(String id,String userName,String email,int balance,String password, List<String> purchaseLog){
        this.id=id;
        this.userName=userName;
        this.email=email;
        this.balance=balance;
        this.password=password;
        this.purchaseLog=purchaseLog;
    }
    public int getBalance(){
        return this.balance;
    }
    public String getUserName(){
        return this.userName;
    }
    public List<String> getPurchaseLog(){
        return this.purchaseLog;
    }

}
