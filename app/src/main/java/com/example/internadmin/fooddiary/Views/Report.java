package com.example.internadmin.fooddiary.Views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.internadmin.fooddiary.Barchart;
import com.example.internadmin.fooddiary.DBHandler;
import com.example.internadmin.fooddiary.Models.Column;
import com.example.internadmin.fooddiary.Models.DishID;
import com.example.internadmin.fooddiary.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Report extends Fragment {

    private ReportRecyclerView mRecyclerView;
    private ReportRecyclerView.Adapter mAdapter;
    private ReportRecyclerView.LayoutManager mLayoutManager;
    private int reportType = 0;

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

        final ArrayList<Bundle> myDataset = new ArrayList<>();
        myDataset.addAll(getReport(reportType, 0, true));

        mAdapter = new ReportRecyclerViewAdapter(myDataset, new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        reportType = 0;
                        myDataset.clear();
                        myDataset.addAll(getReport(reportType, 0, true));
                        mAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        reportType = 1;
                        myDataset.clear();
                        myDataset.addAll(getReport(reportType, 0, true));
                        mAdapter.notifyDataSetChanged();
                        break;
                    case 2:
                        reportType = 2;
                        myDataset.clear();
                        myDataset.addAll(getReport(reportType, 0, true));
                        mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        }, new AdapterView.OnItemSelectedListener() {
            int check = 0;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if(++check > 1){
                        myDataset.clear();
                        //In here, the position of the spinner string determines how long AGO
                        //(EMPHASIS ON AGO, ie. position 1 is t=-1)
                        //the time to be returned should be.
                        myDataset.addAll(getReport(reportType, position, false));
                        mAdapter.notifyDataSetChanged();
                        check = 0;
                    }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }, getContext());

        mRecyclerView.setAdapter(mAdapter);

    }

    private ArrayList<Bundle> getReport(int reportType, int timeAgo, Boolean updateSpinner){
        switch (reportType){
            case 0:
                return dailyReport(timeAgo, updateSpinner);
            case 1:
                return weeklyReport(timeAgo, updateSpinner);
            case 2:
                return monthlyReport(timeAgo, updateSpinner);
            default:
                return dailyReport(timeAgo, updateSpinner);
        }
    }

    private ArrayList<Bundle> dailyReport(int timeAgo, Boolean updateSpinner){
        ArrayList<Bundle> report = new ArrayList<>();

        Calendar currentday = Calendar.getInstance();
        currentday.add(Calendar.DATE, -timeAgo);

        SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM y");

        Bundle headercard = new Bundle();
        ArrayList<String> days = new ArrayList<>();
        days.add("Today");
        days.add("Yesterday");
        days.add("2 days ago");
        days.add("3 days ago");
        headercard.putString("Title", "Daily Scorecard");
        headercard.putString("Content", dateFormat.format(currentday.getTime()));
        headercard.putStringArrayList("spinnerkeys", days);
        headercard.putInt("ViewType", 0);
        headercard.putBoolean("UpdateSpinner", updateSpinner);
        headercard.putInt("SelectedTime", timeAgo);
        headercard.putInt("SelectedPeriod", 0);

        report.add(headercard);

        Calendar dayBefore = (Calendar) currentday.clone();
        dayBefore.add(Calendar.DATE, -1);

        DBHandler handler = new DBHandler(getContext());
        /*List<Long> mealsOnDay = handler.getHistoryEntriesOnDay(currentday.getTime(), null);

        ArrayList<Meal> meallist = new ArrayList<>();
        for(int i = 0; i < mealsOnDay.size(); i++){
            Long id = mealsOnDay.get(i);
            Meal meal = new Meal();
            meal.populateFromDatabase(id, getContext());
            meallist.add(meal);
        }*/

        HashMap<String, Float> servingslist = handler
                .getAllServingsOnDay(currentday.getTime(), null);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        if(servingslist.isEmpty()){
            Bundle noEntriesToday = new Bundle();
            noEntriesToday.putInt("ViewType", -1);
            noEntriesToday.putString("DefaultTitle", "No Entries Added Today.");
            noEntriesToday.putString("DefaultContent", "Start recording your Food Diary!");
            report.add(noEntriesToday);

            return report;
        }

        if(pref.getBoolean("weightloss", false)){

            Pair<String, Float> mynutrition = getdaynutrition(servingslist, "Energy");
            String maxContributingFood = mynutrition.first;
            float consumedCalories = mynutrition.second;
            float caloriesLimit = (float) pref.getInt(getString(R.string.calorielimit), 2000);

            Bundle tryhardercard = new Bundle();

            if(consumedCalories <= caloriesLimit){
                tryhardercard.putInt("ViewType", 1);
                tryhardercard.putString("Title", getString(R.string.congratulations));
                if(timeAgo == 0)
                    tryhardercard.putString("Content", String.format(getString(R.string.congratContentnow), getString(R.string.cal)));
                else
                    tryhardercard.putString("Content", String.format(getString(R.string.congratContentother), getString(R.string.cal)));
                tryhardercard.putString("Subcontent", String.format(getString(R.string.congratSubcontent), Math.round(caloriesLimit - consumedCalories), getString(R.string.cal)));
                report.add(tryhardercard);
            }else{
                tryhardercard.putInt("ViewType", 2);
                tryhardercard.putString("Title", getString(R.string.tryharder));
                tryhardercard.putString("Content", String.format(getString(R.string.tryharderContent), getString(R.string.cal)));
                tryhardercard.putString("Subcontent", String.format(getString(R.string.tryharderSubcontent), Math.round(consumedCalories - caloriesLimit), getString(R.string.cal)));
                report.add(tryhardercard);

                Bundle advicecard = new Bundle();
                advicecard.putString("Title", "Things to Note");
                advicecard.putString("Content", String.format("%s is the largest contributor in %s.",
                        DisplayFoodName(maxContributingFood), getString(R.string.cal)));
                advicecard.putString("Subcontent", "Cut down on this for better results!");
                report.add(advicecard);
            }


        }

        /*

        if(pref.getBoolean("diabetes", false)){
            //TODO: add Congratulatory Card
        }else{
            //TODO: Add Try Harder Card
            //TODO: Add Recommendation Card (What foods should eat less?)
        }*/

        return report;


    }

    private ArrayList<Bundle> weeklyReport(int timeAgo, Boolean updateSpinner){
        ArrayList<Bundle> report = new ArrayList<>();

        Calendar currentweek = Calendar.getInstance();
        currentweek.add(Calendar.WEEK_OF_YEAR, -timeAgo);
        currentweek.set(Calendar.DAY_OF_WEEK, currentweek.getFirstDayOfWeek());

        Bundle headercard = new Bundle();
        ArrayList<String> weeks = new ArrayList<>();
        weeks.add("This week");
        weeks.add("Last week");
        weeks.add("2 weeks ago");
        weeks.add("3 weeks ago");
        headercard.putString("Title", "Weekly Scorecard");
        headercard.putString("Content", "Week " + currentweek.get(Calendar.WEEK_OF_YEAR)
                + ", " + currentweek.get(Calendar.YEAR));
        headercard.putStringArrayList("spinnerkeys", weeks);
        headercard.putInt("ViewType", 0);
        headercard.putBoolean("UpdateSpinner", updateSpinner);
        headercard.putInt("SelectedTime", timeAgo);
        headercard.putInt("SelectedPeriod", 1);

        report.add(headercard);

        Calendar endofWeek = (Calendar) currentweek.clone();
        endofWeek.add(Calendar.WEEK_OF_YEAR, 1);
        endofWeek.add(Calendar.DATE, -1);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());


        if(pref.getBoolean("weightloss", false)){

            ArrayList<Column> columns =  populateBarchart(currentweek, endofWeek, "Energy");
            columns.remove(columns.size()-1);
            float caloriesLimit = (float) pref.getInt(getString(R.string.calorielimit), 2000);

            Bundle b = new Bundle();

            b.putInt("ViewType", 4);
            b.putString("Title", "Weekly Report (Calories)");
            b.putString("Label", "Calories");
            b.putParcelableArrayList("BarData", columns);
            b.putFloat("LimitLineVal", caloriesLimit);

            report.add(b);

            /*

            if(consumedCalories <= caloriesLimit){
                b.putInt("ViewType", 1);
                //TODO: Add Congratulatory Card (You are currently meeting the target Calorie intake!)
            }else{
                b.putInt("ViewType", 2);
                //TODO: Add Try Harder Card
                //TODO: Add Recommendation Card (What foods should eat less?)
            }*/


        }

        /*

        if(pref.getBoolean("diabetes", false)){
            //TODO: add Congratulatory Card
        }else{
            //TODO: Add Try Harder Card
            //TODO: Add Recommendation Card (What foods should eat less?)
        }*/

        return report;


    }

    private ArrayList<Bundle> monthlyReport(int timeAgo, Boolean updateSpinner){
        ArrayList<Bundle> report = new ArrayList<>();

        Calendar currentmonth = Calendar.getInstance();
        currentmonth.add(Calendar.MONTH, -timeAgo);
        currentmonth.set(Calendar.DAY_OF_MONTH, 1);

        Bundle headercard = new Bundle();
        ArrayList<String> weeks = new ArrayList<>();
        weeks.add("This month");
        weeks.add("Last month");
        weeks.add("2 months ago");
        weeks.add("3 months ago");
        headercard.putString("Title", "Monthly Scorecard");
        headercard.putString("Content", currentmonth.getDisplayName(Calendar.MONTH,
                Calendar.LONG, getResources().getConfiguration().locale)
                + ", " + currentmonth.get(Calendar.YEAR));
        headercard.putStringArrayList("spinnerkeys", weeks);
        headercard.putInt("ViewType", 0);
        headercard.putBoolean("UpdateSpinner", updateSpinner);
        headercard.putInt("SelectedTime", timeAgo);
        headercard.putInt("SelectedPeriod", 2);

        report.add(headercard);

        Calendar monthBefore = (Calendar) currentmonth.clone();
        monthBefore.add(Calendar.MONTH, -1);

        DBHandler handler = new DBHandler(getContext());

        HashMap<String, Float> servingslist = handler
                .getAllServingsOnDay(currentmonth.getTime(), null);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        if(servingslist.isEmpty()){
            Bundle noEntriesToday = new Bundle();
            noEntriesToday.putInt("ViewType", -1);
            noEntriesToday.putString("DefaultTitle", "No Entries Added This Month.");
            noEntriesToday.putString("DefaultContent", "Start recording your Food Diary!");
            report.add(noEntriesToday);

            return report;
        }

        if(pref.getBoolean("weightloss", false)){

            Pair<String, Float> mynutrition = getdaynutrition(servingslist, "Energy");

            float caloriesLimit = (float) pref.getInt(getString(R.string.calorielimit), 2000);
            String maxContributingFood = mynutrition.first;
            float consumedCalories = mynutrition.second;
            Bundle b = new Bundle();

            if(consumedCalories <= caloriesLimit){
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



    public Pair<String, Float> getdaynutrition(HashMap<String, Float> servings, String nutritionName){

        Iterator it = servings.entrySet().iterator();
        float tot = 0;
        float max = 0;
        String foodNameMax = "FoodNameError";
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            DishID id = new DishID((String) pair.getKey(), -1, getContext());
            id.execute();
            JsonObject nutrition = id.getNutrition();
            Log.d("jsoncheck", nutrition.toString());
            JsonElement nutritionVal = nutrition.get(nutritionName);
            if(nutritionVal != null){
                float amt = nutritionVal.getAsFloat()*(float)pair.getValue();
                if(amt > max){
                    max = amt;
                    foodNameMax = (String) pair.getKey();
                }
                tot += amt;
            }
        }
        return new Pair<>(foodNameMax, tot);
    }

    public ArrayList<Column> populateBarchart(Calendar startdate, Calendar enddate,
                                              String nutritionName){
        ArrayList<Column> columns = new ArrayList<>();
        DBHandler handler = new DBHandler(getContext());

        HashMap<String, DishID> dishIDHashMap = new HashMap<>();
        HashMap<String, Float> totalNutritionValbyFoodName = new HashMap<>();

        while(!enddate.before(startdate)){

            HashMap<String, Float> servings = handler
                    .getAllServingsOnDay(enddate.getTime(), null);

            Iterator it = servings.entrySet().iterator();

            float dailytotal = 0;
            while (it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                DishID id = dishIDHashMap.get(pair.getKey());
                if( id == null){
                    DishID mydishid = new DishID((String) pair.getKey(),
                            -1, getContext());
                    mydishid.execute();
                    dishIDHashMap.put((String) pair.getKey(), mydishid);
                    id = mydishid;
                }
                JsonObject nutrition = id.getNutrition();
                Log.d("jsoncheck", nutrition.toString());
                JsonElement nutritionVal = nutrition.get(nutritionName);
                if(nutritionVal != null){
                    float amt = nutritionVal.getAsFloat()*(float)pair.getValue();
                    dailytotal += amt;

                    if(totalNutritionValbyFoodName.containsKey(pair.getKey())){
                        totalNutritionValbyFoodName.put((String)pair.getKey(),
                                totalNutritionValbyFoodName.get(pair.getKey()) + amt);
                    }else{
                        totalNutritionValbyFoodName.put((String)pair.getKey(), amt);
                    }
                }
            }

            columns.add(new Column(enddate.getDisplayName(Calendar.DAY_OF_WEEK,
                    Calendar.SHORT, getResources().getConfiguration().locale), dailytotal));

            enddate.add(Calendar.DATE, -1);
        }

        //WARNING: THIS IS NOT A DAY COLUMN AND MUST BE REMOVED.
        //This is a column to get the food type that contributes the most of a
        //nutrition value, and serves to improve computation efficiency by reducing
        //number of database calls.
        Map.Entry<String, Float> maxEntry = null;

        for (Map.Entry<String, Float> entry : totalNutritionValbyFoodName.entrySet())
        {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }

        if(maxEntry == null)
            columns.add(new Column("NoVal", 0f));
        else
            columns.add(new Column(maxEntry.getKey(), maxEntry.getValue()));

        return columns;
    }

    private String DisplayFoodName(String FoodName){
        String[] strArray = FoodName.split("_");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }

        return builder.toString();
    }

}
