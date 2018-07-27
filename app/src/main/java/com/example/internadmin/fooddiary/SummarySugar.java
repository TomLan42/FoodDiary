package com.example.internadmin.fooddiary;


import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.internadmin.fooddiary.Models.DishID;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import devlight.io.library.ArcProgressStackView;

public class SummarySugar extends Fragment {
    // declaring global variables
    DBHandler handler;
    // tracking will be used as query string everywhere.. basically it contains which nutrition to track
    String tracking;
    int limit;
    TextView left;
    int calslimit;
    Calendar myCalendar;
    SharedPreferences prefs;
    ArcProgressStackView arcProgressStackView;
    ArrayList<ArcProgressStackView.Model> models;
    public SummarySugar() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        handler = new DBHandler(getContext());
        return inflater.inflate(R.layout.fragment_summary_sugar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        // tracking Carbohydrates
        tracking = "Carbohydrate";
        calslimit = 2200;
        int diabetesRegime = prefs.getInt(getString(R.string.diabetesregime), 0);

        if(diabetesRegime == 0)
            limit = 45;
        else
            limit = prefs.getInt(getString(R.string.customdiabetesregime), 0);
        left = getView().findViewById(R.id.amount2);
        handler = new DBHandler(getContext());
        myCalendar = Calendar.getInstance();
        arcProgressStackView = view.findViewById(R.id.apsv2);
        updateLabel(myCalendar);

    }

    public void updateLabel(Calendar myCalendar){
        // to update the label in the center of the circular progess bar
        // and to update the value in circular progress bar
        float consumed = getdaycalories(myCalendar, tracking);
        if(consumed < limit ) {
            left.setText(String.valueOf(Math.round(limit - consumed)) + "/" + String.valueOf(limit) + "\n" + tracking + " Left");
        }
        else{
            left.setText(String.valueOf(Math.round(consumed - limit)) + " " + tracking + " Exceeded");
        }
        models = new ArrayList<>();
        if(getdaycalories(myCalendar, "Energy") < calslimit/2 ){
            models.add(new ArcProgressStackView.Model(tracking, Math.round(consumed/22)
                    , getResources().getColor(R.color.progresscolorprimary), getResources().getColor(R.color.progresscolorfillerprimary)));
        }else if(getdaycalories(myCalendar, "Energy") < 3*calslimit/4){

            models.add(new ArcProgressStackView.Model(tracking, Math.round(consumed/22)
                    , getResources().getColor(R.color.progresscolorsecondary), getResources().getColor(R.color.progresscolorfillersecondary)));
        }else{
            models.add(new ArcProgressStackView.Model(tracking, Math.round(consumed/22)
                    , getResources().getColor(R.color.progresscolordanger), getResources().getColor(R.color.progresscolorfillerdanger)));
        }
        arcProgressStackView.setModels(models);
        arcProgressStackView.animateProgress();

    }
    public float getdaycalories(Calendar cal, String getter){
        // function that gets the total amount of "tracking" nutrition consumed in that day
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Calendar tom = (GregorianCalendar) cal.clone();
        tom.set(Calendar.HOUR_OF_DAY, 23);
        tom.set(Calendar.MINUTE, 59);
        tom.set(Calendar.SECOND, 59);
        // dbhandler function for getting id of all dishes in the particular range
        HashMap<String, Float> servings = handler.getAllServingsTimePeriod(cal.getTime(), tom.getTime());
        Iterator it = servings.entrySet().iterator();
        float tot = 0;
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            DishID id = new DishID((String) pair.getKey(), -1, getContext());
            id.execute();
            JsonObject nutrition = id.getNutrition();
            Log.d("jsoncheck", nutrition.toString());
            float calorie = nutrition.get(getter).getAsFloat();
            tot += calorie*(float)pair.getValue();
        }
        return tot;
    }

    @Override
    public void onResume(){
        super.onResume();
        arcProgressStackView.animateProgress();
    }
}
