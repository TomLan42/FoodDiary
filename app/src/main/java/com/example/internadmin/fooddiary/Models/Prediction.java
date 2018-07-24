package com.example.internadmin.fooddiary.Models;

import java.io.Serializable;

/**
 * A class which is used to contain the prediction
 * information from the server.
 *
 * Includes the version number of the DishID in server
 * (in the event the DishID needs to be updated), and
 * the FoodName.
 */

public class Prediction implements Serializable {
    private String FoodName;
    private int ver;

    public Prediction(String FoodName, int ver){
        this.FoodName = FoodName;
        this.ver = ver;
    }

    //Converts underscores in the FoodName to spaces.
    //For displaying on the UI.
    public String getDisplayFoodName(){
        String[] strArray = FoodName.split("_");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }

        return builder.toString();
    }

    public String getFoodName(){
        return FoodName;
    }

    public int getVer(){
        return ver;
    }
}
