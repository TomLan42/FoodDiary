package com.example.internadmin.fooddiary;

import java.io.Serializable;

public class Prediction implements Serializable {
    private String FoodName;
    private int ver;

    Prediction(String FoodName, int ver){
        this.FoodName = FoodName;
        this.ver = ver;
    }

    public String getFoodName(){
        String[] strArray = FoodName.split("_");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }

        return builder.toString();
    }

    public String getInternalFoodName(){
        return FoodName;
    }

    public int getVer(){
        return ver;
    }
}
