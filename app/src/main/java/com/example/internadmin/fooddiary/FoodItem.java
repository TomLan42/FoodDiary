package com.example.internadmin.fooddiary;

public class FoodItem {
    private String title;
    private String content;
    private long id;

    public FoodItem(String title, String content, long id){
        this.title = title;
        this.content = content;
        this.id = id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getId(){ return id; }

    public void setId(long id){ this.id = id; }
}
