package com.example.internadmin.fooddiary.Models;

public class NutritionDefaults {
    private String nutrition;
    private float defaultvalue;
    private String internalNutrition;

    public NutritionDefaults(String nutrition, float defaultvalue,
                             String internalNutrition){
        this.nutrition = nutrition;
        this.defaultvalue = defaultvalue;
        this.internalNutrition = internalNutrition;
    }

    public String getNutrition(){ return nutrition; }

    public float getDefaultvalue(){
        return defaultvalue;
    }

    public String getInternalNutrition(){ return internalNutrition; }

    @Override
    public String toString() {
        return nutrition;
    }
}