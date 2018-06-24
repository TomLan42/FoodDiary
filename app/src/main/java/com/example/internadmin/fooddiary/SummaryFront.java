package com.example.internadmin.fooddiary;


import android.app.DatePickerDialog;
import android.os.Bundle;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SummaryFront extends Fragment {
    DBHandler handler;
    TextView dateselect;
    TextView left;
    Calendar myCalendar;
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
        return inflater.inflate(R.layout.fragment_summary_front, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView record = getView().findViewById(R.id.record);
        left = getView().findViewById(R.id.amount);
        handler = new DBHandler(getContext());
        float consumed = getdaycalories();
        left.setText(String.valueOf(2200-consumed));
        record.setText("Recording Calories");
        dateselect = getView().findViewById(R.id.date);
        setDate();
        setDateListener();
    }
    public void setDateListener(){
        myCalendar = new GregorianCalendar();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        dateselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    private void updateLabel(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        dateselect.setText(sdf.format(myCalendar.getTime()));
        left.setText(String.valueOf(2200-getdaycalories(myCalendar)));
    }
    public void setDate(){
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
        String today = fmt.format(new Date());
        dateselect.setText(today);
    }
    public float getdaycalories(Calendar cal){
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Calendar tom = (GregorianCalendar) cal.clone();
        tom.set(Calendar.HOUR_OF_DAY, 23);
        tom.set(Calendar.MINUTE, 59);
        tom.set(Calendar.SECOND, 59);
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
    }
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
    }
}
