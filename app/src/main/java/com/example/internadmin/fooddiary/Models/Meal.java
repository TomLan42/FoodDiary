package com.example.internadmin.fooddiary.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.internadmin.fooddiary.DBHandler;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

public class Meal implements Serializable{

    private DishID MyDishID;
    private Date TimeConsumed;
    private float ServingAmt;
    private File FoodImg;
    private Long RowID;

    public Meal(DishID MyDishID, Date TimeConsumed, float ServingAmt){

        this.MyDishID = MyDishID;
        this.TimeConsumed = TimeConsumed;
        this.ServingAmt = ServingAmt;

    }

    public Meal(){
    }

    public void setFoodImg(File foodImg) {
        this.FoodImg = foodImg;
    }

    public boolean saveToDatabase(Context ctx){

        DBHandler mydbhandler = new DBHandler(ctx);

        return mydbhandler.insertMealEntry(MyDishID.getInternalFoodName(), TimeConsumed, ServingAmt, FoodImg);
    }

    public void populateFromDatabase(long MealID, Context ctx){

        DBHandler mydbhandler = new DBHandler(ctx);

        Bundle b = mydbhandler.getMeal(MealID);
        this.MyDishID = new DishID(b.getString("FoodName"), -1, ctx);
        this.MyDishID.execute();
        this.TimeConsumed = (Date) b.getSerializable("TimeConsumed");
        if(b.containsKey("FoodImg")){
            this.FoodImg = (File) b.getSerializable("FoodImg");
        }
        this.ServingAmt = b.getFloat("ServingAmt");
        this.RowID = MealID;

    }

    public boolean updateInDatabase(Context ctx){

        DBHandler mydbhandler = new DBHandler(ctx);

        return mydbhandler.updateHistoryEntry(MyDishID.getInternalFoodName(), TimeConsumed, ServingAmt, RowID);
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

    public boolean deleteFoodImg(){

        return this.FoodImg == null || this.FoodImg.delete();

    }

    public boolean deleteMeal(Context context){
        if(deleteFoodImg()) {
            if (RowID == null) {
                return true;
            } else {
                DBHandler handler = new DBHandler(context);
                return handler.deleteHistoryEntry(RowID);
            }
        }
        return false;
    }

}