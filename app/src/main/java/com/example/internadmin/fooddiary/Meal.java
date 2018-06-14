package com.example.internadmin.fooddiary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

public class Meal implements Serializable{

    private DishID MyDishID;
    private Date TimeConsumed;
    private float ServingAmt;
    private File FoodImg;
    private long RowID;
    private DBHandler mydbhandler;
    private Context ctx;

    public Meal(){

    }

    public Meal(DishID MyDishID, Context ctx, Date TimeConsumed, float ServingAmt){

        this.MyDishID = MyDishID;
        this.TimeConsumed = TimeConsumed;
        this.ServingAmt = ServingAmt;
        this.ctx = ctx;

    }


    public void setFoodImg(File foodImg) {
        FoodImg = foodImg;
    }

    public boolean saveToDatabase(){

        mydbhandler = new DBHandler(ctx);

        return mydbhandler.insertMealEntry(MyDishID.getFoodName(), TimeConsumed, ServingAmt, FoodImg);
    }

    public void populateFromDatabase(long MealID, Context ctx){
        this.ctx = ctx;
        mydbhandler = new DBHandler(ctx);

        Bundle b = mydbhandler.getMeal(MealID);
        MyDishID = new DishID(b.getString("FoodName"), -1, ctx);
        this.TimeConsumed = (Date) b.getSerializable("TimeConsumed");
        if(b.containsKey("FoodImg")){
            this.FoodImg = (File) b.getSerializable("FoodImg");
        }
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

