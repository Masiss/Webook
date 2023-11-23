package com.example.webook.model;

import androidx.annotation.NonNull;
import androidx.navigation.NavType;

import java.io.Serializable;
import java.util.List;

public class Book implements Serializable {
    private String id;
    private String image;
    private String title;
    private String author;
    private String description;
    private String shortDescription;
    private List<String> chapter;
    private String content;
    private int price;
    private int sold;

    public Book() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public Book(String id, String image, String title, String author, String description, String shortDescription, List<String> chapter, String content, int price) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.author = author;
        this.description = description;
        this.shortDescription = shortDescription;
        this.chapter = chapter;
        this.content = content;
        this.price = price;
    }


    @NonNull
    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getChapter() {
        return chapter;
    }

    public String getContent() {
        return content;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setChapter(List<String> chapter) {
        this.chapter = chapter;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public int getSold() {
        return sold;
    }
}
