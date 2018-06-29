package com.example.internadmin.fooddiary.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.example.internadmin.fooddiary.DBHandler;
import com.example.internadmin.fooddiary.R;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Meal{

    private DishID MyDishID;
    private Date TimeConsumed;
    private float ServingAmt;
    private File FoodImg;
    private Long RowID;
    private TimePeriod timePeriod;


    public Meal(DishID MyDishID, Date TimeConsumed, float ServingAmt, Context ctx){

        this.MyDishID = MyDishID;
        this.TimeConsumed = TimeConsumed;
        this.ServingAmt = ServingAmt;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(TimeConsumed);
        int[] mytime = {calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)};

        int[] morning = {
                prefs.getInt(ctx.getResources().getString(R.string.morning_start_hour), 0),
                prefs.getInt(ctx.getResources().getString(R.string.morning_start_min), 0),
                prefs.getInt(ctx.getResources().getString(R.string.morning_end_hour), 12),
                prefs.getInt(ctx.getResources().getString(R.string.morning_end_min), 0)};
        int[] afternoon = {
                prefs.getInt(ctx.getResources().getString(R.string.afternoon_start_hour), 12),
                prefs.getInt(ctx.getResources().getString(R.string.afternoon_start_min), 0),
                prefs.getInt(ctx.getResources().getString(R.string.afternoon_end_hour), 17),
                prefs.getInt(ctx.getResources().getString(R.string.afternoon_end_min), 0)};
        int[] evening = {
                prefs.getInt(ctx.getResources().getString(R.string.evening_start_hour), 17),
                prefs.getInt(ctx.getResources().getString(R.string.evening_start_min), 0),
                prefs.getInt(ctx.getResources().getString(R.string.evening_end_hour), 21),
                prefs.getInt(ctx.getResources().getString(R.string.evening_end_min), 0)};
        int[] night = {
                prefs.getInt(ctx.getResources().getString(R.string.night_start_hour), 21),
                prefs.getInt(ctx.getResources().getString(R.string.night_start_min), 0),
                prefs.getInt(ctx.getResources().getString(R.string.night_end_hour), 23),
                prefs.getInt(ctx.getResources().getString(R.string.night_end_min), 59)};

        if (inTimeRange(mytime, morning))
            timePeriod = TimePeriod.MORNING;
        else if (inTimeRange(mytime, afternoon))
            timePeriod = TimePeriod.AFTERNOON;
        else if (inTimeRange(mytime, evening))
            timePeriod = TimePeriod.EVENING;
        else if(inTimeRange(mytime, night))
            timePeriod = TimePeriod.NIGHT;
        else
            timePeriod = TimePeriod.UNKNOWN;

    }

    public Meal(DishID MyDishID, Date TimeConsumed, TimePeriod timePeriod, float ServingAmt){

        this.MyDishID = MyDishID;
        this.TimeConsumed = TimeConsumed;
        this.ServingAmt = ServingAmt;
        this.timePeriod = timePeriod;

    }

    public Meal(){
    }

    public void setFoodImg(File foodImg) {
        this.FoodImg = foodImg;
    }

    public boolean saveToDatabase(Context ctx){

        DBHandler mydbhandler = new DBHandler(ctx);

        return mydbhandler.insertMealEntry(MyDishID.getInternalFoodName(), TimeConsumed, timePeriod, ServingAmt, FoodImg);
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
        this.timePeriod = TimePeriod.values()[b.getInt("TimePeriod", 0)];

    }

    public boolean updateInDatabase(Context ctx){

        DBHandler mydbhandler = new DBHandler(ctx);

        return mydbhandler.updateHistoryEntry(MyDishID.getInternalFoodName(), TimeConsumed, timePeriod, ServingAmt, RowID);
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

    public TimePeriod getTimePeriod() {
        return timePeriod;
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

    private Boolean inTimeRange(int[] giventime, int[] timerange){

        if(timerange[0] > timerange[2])
            timerange[2] += 24;

        if((timerange[0] < giventime[0]) && (giventime[0] < timerange[2]))
            return true;
        else if (timerange[0] == giventime[0]){

            return (timerange[1] <= giventime[1]);

        }else if (timerange[2] == giventime[0]){

            return (timerange[3] > giventime[1]);

        }
        return false;

    }

}