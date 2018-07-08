package com.example.internadmin.fooddiary.Views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.internadmin.fooddiary.DBHandler;
import com.example.internadmin.fooddiary.Models.DishID;
import com.example.internadmin.fooddiary.Models.FoodItem;
import com.example.internadmin.fooddiary.Models.Meal;
import com.example.internadmin.fooddiary.Models.TimePeriod;
import com.example.internadmin.fooddiary.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Report extends Fragment {

    private ReportRecyclerView mRecyclerView;
    private ReportRecyclerView.Adapter mAdapter;
    private ReportRecyclerView.LayoutManager mLayoutManager;

    public Report() {
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
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = view.findViewById(R.id.reportrecyclerview);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Bundle b = new Bundle();
        b.putString("Title", "WASSSUPPPP");
        b.putString("Content", "MY MANNNNN");

        final ArrayList<Bundle> myDataset = new ArrayList<>();
        myDataset.add(b);
        myDataset.add(new Bundle());
        myDataset.add(new Bundle());

        final ArrayList<Bundle> anotherDataset = new ArrayList<>();
        anotherDataset.add(new Bundle());
        anotherDataset.add(b);
        anotherDataset.add(new Bundle());

        final ArrayList<Bundle> someDataset = new ArrayList<>();
        someDataset.add(b);
        someDataset.add(new Bundle());
        someDataset.add(new Bundle());
        someDataset.add(b);




        // specify an adapter (see also next example)
        mAdapter = new ReportRecyclerViewAdapter(myDataset, new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        myDataset.clear();
                        myDataset.addAll(someDataset);
                        mAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        myDataset.clear();
                        myDataset.addAll(anotherDataset);
                        mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mRecyclerView.setAdapter(mAdapter);

    }

    private ArrayList<Bundle> getReport(int reportType, int time){
        switch (reportType){
            /*case 0:
                return dailyReport(time);
            case 1:
                return weeklyReport(time);
            case 2:
                return monthlyReport(time);*/
            default:
                ArrayList<Bundle> headerOnlydataset = new ArrayList<>();
                //headerOnlydataset.add(headerCard);
                return headerOnlydataset;
        }
    }

    private ArrayList<Bundle> dailyReport(int time){
        ArrayList<Bundle> report = new ArrayList<>();
        //TODO: Add headercard

        Calendar currentday = Calendar.getInstance();
        currentday.add(Calendar.DATE, time);

        Calendar dayBefore = (Calendar) currentday.clone();
        dayBefore.add(Calendar.DATE, -1);

        DBHandler handler = new DBHandler(getContext());
        List<Long> mealsOnDay = handler.getHistoryEntriesOnDay(currentday.getTime(), null);

        ArrayList<Meal> meallist = new ArrayList<>();
        for(int i = 0; i < mealsOnDay.size(); i++){
            Long id = mealsOnDay.get(i);
            Meal meal = new Meal();
            meal.populateFromDatabase(id, getContext());
            meallist.add(meal);
        }

        HashMap<String, Float> servingslist = handler
                .getAllServingsOnDay(currentday.getTime(), null);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        if(servingslist.isEmpty()){
            Bundle noEntriesToday = new Bundle();
            report.add(noEntriesToday);
            return report;
        }

        if(pref.getBoolean("weightloss", false)){

            float consumedCalories = getdaynutrition(servingslist, "Energy");
            float caloriesLimit = (float) pref.getInt(getString(R.string.tracking_nutrition_limit), 2000);

            Bundle b = new Bundle();

            if(consumedCalories <= caloriesLimit){
                b.putInt("ViewType", 1);
                //TODO: Add Congratulatory Card (You are currently meeting the target Calorie intake!)
            }else{
                b.putInt("ViewType", 2);
                //TODO: Add Try Harder Card
                //TODO: Add Recommendation Card (What foods should eat less?)
            }


        }

        if(pref.getBoolean("diabetes", false)){
            //TODO: add Congratulatory Card
        }else{
            //TODO: Add Try Harder Card
            //TODO: Add Recommendation Card (What foods should eat less?)
        }

        return report;


    }

    public float getdaynutrition(HashMap<String, Float> servings, String nutritionName){

        Iterator it = servings.entrySet().iterator();
        float tot = 0;
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            DishID id = new DishID((String) pair.getKey(), -1, getContext());
            id.execute();
            JsonObject nutrition = id.getNutrition();
            Log.d("jsoncheck", nutrition.toString());
            JsonElement nutritionVal = nutrition.get(nutritionName);
            if(nutritionVal != null)
                tot += nutritionVal.getAsFloat()*(float)pair.getValue();
        }
        return tot;
    }

}
