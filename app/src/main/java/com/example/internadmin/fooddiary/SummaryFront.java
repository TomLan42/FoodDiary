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

public class SummaryFront extends Fragment {
    DBHandler handler;
    //TextView dateselect;
    String tracking;
    int limit;
    TextView left;
    Calendar myCalendar;
    public int totalheight;
    SharedPreferences prefs;
    ArcProgressStackView arcProgressStackView;
    ArrayList<ArcProgressStackView.Model> models;
    public SummaryFront() {
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
        return inflater.inflate(R.layout.fragment_summary_front, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        tracking = "Energy";
        limit = (int)prefs.getFloat(getString(R.string.tracking_nutrition_limit), 0);
        //TextView record = getView().findViewById(R.id.record);
        left = getView().findViewById(R.id.amount);
        handler = new DBHandler(getContext());
        myCalendar = Calendar.getInstance();
        //float consumed = getdaycalories();
        //left.setText(String.valueOf(2200-consumed));
        //record.setText("Recording " + tracking);
        ///dateselect = getView().findViewById(R.id.date);
        //dateselect.setBackgroundResource(R.color.colorAccent);

        //setDateListener();

        arcProgressStackView = view.findViewById(R.id.apsv);
        //totalheight = dateselect.getHeight()*3 + arcProgressStackView.getHeight() + record.getHeight()*3;
        updateLabel(myCalendar);



    }

    public void updateLabel(Calendar myCalendar){
        //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        //dateselect.setText(sdf.format(myCalendar.getTime()));
        float consumed = getdaycalories(myCalendar);
        left.setText(String.valueOf(Math.round(limit-consumed)) +"/" +String.valueOf(limit) +"\n"+tracking + " Left");

        models = new ArrayList<>();
        models.add(new ArcProgressStackView.Model(tracking, Math.round(consumed/22)
                , Color.parseColor("#90ee90"), Color.parseColor("#228B22")));

        arcProgressStackView.setModels(models);
        arcProgressStackView.animateProgress();

    }
    public float getdaycalories(Calendar cal){
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Calendar tom = (GregorianCalendar) cal.clone();
        tom.set(Calendar.HOUR_OF_DAY, 23);
        tom.set(Calendar.MINUTE, 59);
        tom.set(Calendar.SECOND, 59);
        HashMap<String, Float> servings = handler.getAllServingsOnDay(cal.getTime(), null);
        Iterator it = servings.entrySet().iterator();
        float tot = 0;
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            DishID id = new DishID((String) pair.getKey(), -1, getContext());
            id.execute();
            JsonObject nutrition = id.getNutrition();
            Log.d("jsoncheck", nutrition.toString());
            float calorie = nutrition.get(tracking).getAsFloat();
            tot += calorie*(float)pair.getValue();
        }
        return tot;
    }

    @Override
    public void onResume(){
        super.onResume();
        arcProgressStackView.animateProgress();
    }
    /*
    public float getdaycalories() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Calendar tom = new GregorianCalendar();
        tom.setTime(new Date());
        HashMap<String, Float> servings = handler.getAllServingsTimePeriod(cal.getTime(), tom.getTime());
        Iterator it = servings.entrySet().iterator();
        float tot = 0;
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            DishID id = new DishID((String) pair.getKey(), -1, getContext());
            id.execute();
            JsonObject nutrition = id.getNutrition();
            Log.d("jsoncheck", nutrition.toString());
            float calorie = nutrition.get("Energy").getAsFloat();
            tot += calorie*(float)pair.getValue();
        }
        return tot;
    }*/
}
