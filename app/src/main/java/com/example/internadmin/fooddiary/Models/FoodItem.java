package com.example.internadmin.fooddiary.Models;

// This is the adapter for food item list shown in meal cards
public class FoodItem {
    private String title;
    private long id;

    public FoodItem(String title, String content, long id){
        this.title = title;
        this.id = id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) { this.title = title; }

    public long getId(){ return id; }

    public void setId(long id){ this.id = id; }
}
