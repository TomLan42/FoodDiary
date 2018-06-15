package com.example.internadmin.fooddiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DishID implements PostTaskListener<Bundle> {

    private String FoodName;
    private int ver;
    private transient JsonObject Nutrition;
    private List<String> Ingredients;
    private File FoodImg;
    private Context ctx;
    private DishIDPopulatedListener listener;

    public DishID(String FoodName, int ver, Context ctx){
        this.FoodName = FoodName;
        this.ver = ver;
        this.ctx = ctx;
    }

    public void setDishIDPopulatedListener(DishIDPopulatedListener listener){
        this.listener = listener;
    }

    public void execute(){
        boolean fromDB = populateFromDatabase(FoodName, ver);

        if(!fromDB){
            populateFromOnline(FoodName, ctx);
        }
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

    public JsonObject getNutrition() {
        return Nutrition;
    }

    public List<String> getIngredients() {
        return Ingredients;
    }

    public Bitmap getFoodImg(){
        if(FoodImg == null){
            return BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_error_outline_black);
        }else{
            return BitmapFactory.decodeFile(FoodImg.getAbsolutePath());
        }
    }

    private String Nutjson2str(){
        return Nutrition.toString();
    }

    private String IngList2str(){
        return new Gson().toJson(Ingredients);
    }


    private boolean populateFromDatabase(String FoodName, int ver){

        DBHandler mydbhandler = new DBHandler(ctx);
        Bundle b = mydbhandler.getDishID(FoodName);

        if (b.getBoolean("Exists") && b.getInt("Version") >= ver){
            JsonParser parser = new JsonParser();
            this.Nutrition = parser.parse(b.getString("Nutrition")).getAsJsonObject();
            Type category = new TypeToken<List<String>>(){}.getType();
            this.Ingredients = new Gson().fromJson(b.getString("Ingredients"), category);

            this.FoodImg = (File) b.getSerializable("FoodImg");
        }else{
            return false;
        }

        if(listener != null)
            listener.onPopulated(true);

        return true;

    }

    private void populateFromOnline(String FoodName, Context ctx){

        new DownloadDishIDTask(this, ctx, FoodName).execute();

    }

    public void onPostTask(Bundle result){

        if (result.getString(DownloadDishIDTask.Result).equals(DownloadDishIDTask.Success)){
            ver = result.getInt("Version");
            FoodImg = (File) result.getSerializable("FoodImg");
            JsonParser parser = new JsonParser();
            Nutrition = parser.parse(result.getString("Nutrition")).getAsJsonObject();
            JSONArray temparray;
            Ingredients = new ArrayList<>();
            try{
                temparray = new JSONArray(result.getString("Ingredients"));
                for (int i=0; i < temparray.length(); i++)
                    Ingredients.add(temparray.getString(i));
            } catch(JSONException e){
                Log.e("DishID Online", "JSONArray Error:" + e.getMessage());
            }

            saveToDatabase();

            if(listener != null)
                listener.onPopulated(true);

        } else{
            Toast.makeText( ctx, result.getString(DownloadDishIDTask.Result),
                    Toast.LENGTH_LONG).show();

            if(listener != null)
                listener.onPopulated(false);
        }
    }

    private void saveToDatabase(){

        DBHandler mydbhandler = new DBHandler(ctx);
        mydbhandler.insertNewDishID( FoodName, ver, Nutjson2str(), IngList2str(), FoodImg.getAbsolutePath());

    }



}
