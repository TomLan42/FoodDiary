package com.example.internadmin.fooddiary;

import com.example.internadmin.fooddiary.Models.NutritionDefaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Config {
    public static final String getinfoAddress = "http://155.69.53.72:8000/foodpal/getinfo/";
    public static final String getpredictionAddress = "http://155.69.53.72:8000/foodpal/appsubmit/";
    public static final String submitformAddress = "http://155.69.53.72:8000/foodpal/helpformapp/";

    public static List<NutritionDefaults> NUTRITION_DEFAULTS_ARRAY_LIST;

    static {
        List<NutritionDefaults> mylist = new ArrayList<>();
        mylist.add(new NutritionDefaults("Calories (kcal)", 2000, "Energy"));
        mylist.add(new NutritionDefaults("Total Fat (g)", 65, "Fat"));
        mylist.add(new NutritionDefaults("Saturated Fats (g)", 20, "Saturated Fat"));
        mylist.add(new NutritionDefaults("Cholesterol (mg)", 300, "Cholesterol"));
        mylist.add(new NutritionDefaults("Sodium (mg)", 2400, "Sodium"));
        //mylist.add(new NutritionDefaults("Potassium (mg)", 3500, "Potassium"));
        mylist.add(new NutritionDefaults("Total Carbohydrate (g)", 300, "Carbohydrate"));
        mylist.add(new NutritionDefaults("Dietary Fibre (g)", 25, "Fibre"));
        mylist.add(new NutritionDefaults("Protein (g)", 50, "Protein"));

        NUTRITION_DEFAULTS_ARRAY_LIST = Collections.unmodifiableList(mylist);
    }
}
