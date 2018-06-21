package com.example.internadmin.fooddiary;

import com.example.internadmin.fooddiary.Models.NutritionDefaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Config {
    public static final String getinfoAddress = "http://155.69.53.72:8000/getinfo/";
    public static final String getpredictionAddress = "http://155.69.53.72:8000/appsubmit/";

    public static List<NutritionDefaults> NUTRITION_DEFAULTS_ARRAY_LIST;

    static {
        List<NutritionDefaults> mylist = new ArrayList<>();
        mylist.add(new NutritionDefaults("hello", 1f));
        NUTRITION_DEFAULTS_ARRAY_LIST = Collections.unmodifiableList(mylist);
    }
}
