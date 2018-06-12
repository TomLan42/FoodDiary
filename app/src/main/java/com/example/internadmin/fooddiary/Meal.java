package com.example.internadmin.fooddiary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class Meal {

    private DishID MyDishID;
    private Date TimeConsumed;
    private float ServingAmt;
    private File FoodImg;
    private long RowID;
    private DBHandler mydbhandler;

    public Meal(String FoodName, int ver, File FoodImg, Context ctx, Date TimeConsumed, float ServingAmt){

        MyDishID = new DishID(FoodName, ver, ctx);
        this.TimeConsumed = TimeConsumed;
        this.ServingAmt = ServingAmt;
        this.FoodImg = FoodImg;


    }

    public boolean saveToDatabase(Context ctx){

        mydbhandler = new DBHandler(ctx);

        return mydbhandler.insertMealEntry(MyDishID.getFoodName(), TimeConsumed, ServingAmt, FoodImg);
    }

    public void populateFromDatabase(long MealID, Context ctx){
        mydbhandler = new DBHandler(ctx);

        Bundle b = mydbhandler.getMeal(MealID);
        MyDishID = new DishID(b.getString("FoodName"), -1, ctx);
        this.TimeConsumed = (Date) b.getSerializable("TimeConsumed");
        this.FoodImg = (File) b.getSerializable("FoodImg");
        this.ServingAmt = b.getFloat("ServingAmt");
        this.RowID = MealID;

    }

    public boolean updateInDatabase(Context ctx){

        return mydbhandler.updateHistoryEntry(MyDishID.getFoodName(), TimeConsumed, ServingAmt, FoodImg, RowID);
    }

    public void setTimeConsumed(Date date){
        TimeConsumed = date;
    }

    public void setServingAmt(float amt){
        ServingAmt = amt;
    }

    public Date getTimeConsumed(){
        return TimeConsumed;
    }


    public Bitmap getFoodImg(){
        if (FoodImg == null){
            return MyDishID.getFoodImg();
        }else{
            return BitmapFactory.decodeFile(FoodImg.getAbsolutePath());
        }
    }

    public float getServingAmt(){
        return ServingAmt;
    }

    public DishID getDishID(){
        return MyDishID;
    }


}

