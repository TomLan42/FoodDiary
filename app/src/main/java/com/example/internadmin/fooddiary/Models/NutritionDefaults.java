package com.example.internadmin.fooddiary.Models;

public class NutritionDefaults {
    String nutrition;
    float defaultvalue;

    public NutritionDefaults(String nutrition, float defaultvalue){
        this.nutrition = nutrition;
        this.defaultvalue = defaultvalue;
    }

    public String getNutrition(){
        return nutrition;
    }

    public float getDefaultvalue(){
        return defaultvalue;
    }
}