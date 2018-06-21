package com.example.internadmin.fooddiary.Views;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.internadmin.fooddiary.DBHandler;
import com.example.internadmin.fooddiary.Activities.MealActivity;
import com.example.internadmin.fooddiary.Models.DishID;
import com.example.internadmin.fooddiary.Models.FoodItem;
import com.example.internadmin.fooddiary.Models.Meal;
import com.example.internadmin.fooddiary.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Summary extends Fragment {
    ScrollView sv;
    CardView barcard;
    DBHandler handler;
    BarChart chart;
    NonScrollListView breakfastlist;
    NonScrollListView lunchlist;
    NonScrollListView dinnerlist;
    Point size;
    public Summary() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            return;


        //setContentView(sv);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // THE MAIN SCROLLVIEW FOR THE WHOLE FRAGMENT
        sv = new ScrollView(getContext());

        // LINEAR LAYOUT TO CONTAIN OTHER VIEWS (SINCE SCROLLVIEW CAN HAVE ONLY ONE VIEW APPENDED IN IT)
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll);

        // DATABASE HANDLER OBJECT
        handler = new DBHandler(getContext());

        // GETTING THE SCREEN SIZE OF THE DEVICE
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        // THE ARRAY TO BE PASSED INTO THE BARCHART FUNCTION TO CREATE BAR CHART
        float[] val = getcaloriearray();
        // FUNCTION TO CREATE BARCHART CARD
        CardView barcard = createChart(val);
        ll.addView(barcard);

        // GETTING THE LIST OF FOOD ITEMS IN BREAKFAST LUNCH AND DINNER
        List<Long> breakfastlist = handler.getHistoryEntries(getBreakFastStart(), getBreakFastEnd());
        List<Long> lunchlist = handler.getHistoryEntries(getLunchStart(), getLunchEnd());
        List<Long> dinnerlist = handler.getHistoryEntries(getDinnerStart(), getDinnerEnd());

        // IF THE LIST CONTAINS NON ZERO NUMBER OF ITEMS THEN CARD FOR THAT MEAL IS CREATED
        if(breakfastlist.size() > 0){
            CardView breakfastcard = createBreakfast(breakfastlist);
            ll.addView(breakfastcard);
        }
        if(lunchlist.size() > 0){
            CardView lunchcard = createLunch(lunchlist);
            ll.addView(lunchcard);
        }
        if(dinnerlist.size() > 0){
            CardView dinnercard = createDinner(dinnerlist);
            ll.addView(dinnercard);
        }

        //sv.addView(fab);
        sv.smoothScrollTo(0, 0);
        return sv;
    }
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public float[] getcaloriearray(){
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        float[] val = {0, 0, 0, 0, 0, 0, 0};
        Calendar today = new GregorianCalendar();
        today.setTime(new Date());
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            Calendar tom = new GregorianCalendar();
            tom = (Calendar) cal.clone();
            tom.add(Calendar.DATE, 1);
            HashMap<String, Float> servings = handler.getAllServingsTimePeriod(cal.getTime(), tom.getTime());
            Iterator it = servings.entrySet().iterator();
            float tot = 0;
            while(it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                DishID id = new DishID((String) pair.getKey(), -1, getContext());
                id.execute();
                JsonObject nutrition = id.getNutrition();
                float calorie = nutrition.get("Calories").getAsFloat();
                tot += calorie*(float)pair.getValue();
            }
            val[0] = tot;
        }
        else{
            cal.add( Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK)-1));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            Calendar tom = new GregorianCalendar();
            int counter = 0;
            while (true){
                tom = (Calendar) cal.clone();
                tom.add(Calendar.DATE, 1);
                HashMap<String, Float> servings = handler.getAllServingsTimePeriod(cal.getTime(), tom.getTime());
                Iterator it = servings.entrySet().iterator();
                float tot = 0;
                while(it.hasNext()){
                    Map.Entry pair = (Map.Entry)it.next();
                    DishID id = new DishID((String) pair.getKey(), -1, getContext());
                    id.execute();
                    JsonObject nutrition = id.getNutrition();
                    Log.d("jsoncheck", nutrition.toString());
                    float calorie = nutrition.get("Energy").getAsFloat();
                    tot += calorie*(float)pair.getValue();
                }
                val[counter] = tot;
                counter += 1;
                Log.d("Time solver", format.format(cal.getTime()));
                Log.d("Time solver 2", format.format(today.getTime()));
                if(isSameDay(cal, today)) break;
                cal.add(Calendar.DATE, 1);
            }
        }
        return val;
    }


    //------------------------------------------------
    //     FUNCTION TO GET BREAKFAST STARTTIME       |
    //------------------------------------------------
    public Date getBreakFastStart(){
        Calendar mealtime = new GregorianCalendar();
        mealtime.setTime(new Date());
        mealtime.set(Calendar.HOUR_OF_DAY, 0);
        mealtime.set(Calendar.MINUTE, 0);
        mealtime.set(Calendar.SECOND, 0);
        return mealtime.getTime();
    }

    //------------------------------------------------
    //     FUNCTION TO GET BREAKFAST ENDTIME       |
    //------------------------------------------------
    public Date getBreakFastEnd(){
        Calendar mealtime = new GregorianCalendar();
        mealtime.setTime(new Date());
        mealtime.set(Calendar.HOUR_OF_DAY, 11);
        mealtime.set(Calendar.MINUTE, 0);
        mealtime.set(Calendar.SECOND, 0);
        return mealtime.getTime();
    }


    //------------------------------------------------
    //     FUNCTION TO GET LUNCH STARTTIME       |
    //------------------------------------------------
    public Date getLunchStart(){
        Calendar mealtime = new GregorianCalendar();
        mealtime.setTime(new Date());
        mealtime.set(Calendar.HOUR_OF_DAY, 11);
        mealtime.set(Calendar.MINUTE, 0);
        mealtime.set(Calendar.SECOND, 0);
        return mealtime.getTime();
    }

    //------------------------------------------------
    //     FUNCTION TO GET LUNCH ENDTIME       |
    //------------------------------------------------
    public Date getLunchEnd(){
        Calendar mealtime = new GregorianCalendar();
        mealtime.setTime(new Date());
        mealtime.set(Calendar.HOUR_OF_DAY, 20);
        mealtime.set(Calendar.MINUTE, 0);
        mealtime.set(Calendar.SECOND, 0);
        return mealtime.getTime();
    }


    //------------------------------------------------
    //     FUNCTION TO GET DINNER STARTTIME       |
    //------------------------------------------------
    public Date getDinnerStart(){
        Calendar mealtime = new GregorianCalendar();
        mealtime.setTime(new Date());
        mealtime.set(Calendar.HOUR_OF_DAY, 20);
        mealtime.set(Calendar.MINUTE, 0);
        mealtime.set(Calendar.SECOND, 0);
        return mealtime.getTime();
    }


    //------------------------------------------------
    //     FUNCTION TO GET DINNER ENDTIME       |
    //------------------------------------------------
    public Date getDinnerEnd(){
        Calendar mealtime = new GregorianCalendar();
        mealtime.setTime(new Date());
        mealtime.set(Calendar.HOUR_OF_DAY, 23);
        mealtime.set(Calendar.MINUTE, 0);
        mealtime.set(Calendar.SECOND, 0);
        return mealtime.getTime();
    }


    public CardView createChart(float[] val){
        /*
        function to create the card view for the barchart and integrate barchart with it.
        Takes input a 2D array containing co-ordinates of top points of bars. (x(vals[0]), y(vals[1]))
        Returns a CardView object.
        */

        // CREATING THE CARD FOR CONTAINING BARCHART
        barcard = new CardView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // SETTING MARGINS TO THE CARD
        int marginx = (int)(size.x*0.027);
        int marginy = (int)(size.x*0.027);
        params.setMargins(marginx, marginy, marginx, marginy);
        // SETTING OTHER LAYOUT PARAMETERS FOR THE CARD
        barcard.setLayoutParams(params);
        barcard.setRadius(0);
        barcard.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));
        barcard.setMaxCardElevation(15);
        barcard.setCardElevation(9);

        // BARCHART OBJECT FOR CREATING BARCHART
        chart = new BarChart(getContext());
        final ArrayList<String> xLabel = getXAxisValues();
        // ARRAY LIST OF DATASET POINTS
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        for(int i = 0; i < 7; i++){
            yVals.add(new BarEntry(i+1, val[i]));
        }
        BarDataSet set1 = new BarDataSet(yVals, "label");
        set1.setColor(R.color.grey);
        dataSets.add(set1);
        BarData data = new BarData(dataSets);


        // SETTING CHART PARAMETERS SO THAT AXIS AND GRID AND LABELS ARE NOT VISIBLE
        chart.setData(data);
        chart.setMinimumHeight((int) (size.y*0.4));
        chart.animateY(1000);
        //chart.getXAxis().setEnabled(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getLegend().setEnabled(false);
        //chart.centerViewTo(1, 1);
        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(2500);
        LimitLine l = new LimitLine(2200, "Max calories (2200)");
        leftAxis.addLimitLine(l);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setAxisMinimum(0);
        leftAxis.setDrawLabels(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int index = (int)value;
                return xLabel.get(index);
            }
        });
        rightAxis.setEnabled(false);
        //leftAxis.setEnabled(false);
        chart.setDrawBorders(false);
        chart.setTouchEnabled(false);
        chart.setMinimumWidth((int) (size.x*0.96));
        // SETTING DESCRIPTION TO NULL
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        barcard.addView(chart);
        return barcard;
    }
    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Sun");
        xAxis.add("Sun");
        xAxis.add("Mon");
        xAxis.add("Tue");
        xAxis.add("Wed");
        xAxis.add("Thu");
        xAxis.add("Fri");
        xAxis.add("Sat");
        return xAxis;
    }

    public CardView createBreakfast(List<Long> mealdata){
        /*
        function to create cardview for breakfast.
        Creates a cardview.
        Structure of cardview:
        CardView:
            LinearLayout:
                RelativeLayout:
                    BreakFast text
                    Image
                ListView
        Returns the cardview
        */
        CardView breakfastcard = new CardView(getContext());
        LinearLayout breakfastlayout = new LinearLayout(getContext());
        breakfastlayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int marginx = (int)(size.x*0.027);
        int marginy = (int)(size.x*0.027);
        params.setMargins(marginx, marginy, marginx, marginy);
        breakfastcard.setLayoutParams(params);
        breakfastcard.setRadius(0);
        breakfastcard.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));
        breakfastcard.setMaxCardElevation(15);
        breakfastcard.setCardElevation(9);
        breakfastcard.addView(breakfastlayout);
        RelativeLayout rl = new RelativeLayout(getContext());
        breakfastlayout.addView(rl);
        TextView breakfasthead = new TextView(getContext());
        breakfasthead.setText("Breakfast");
        breakfasthead.setId(100);
        breakfasthead.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (size.x*0.085));
        RelativeLayout.LayoutParams breakfastparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        breakfastparams.setMargins((int)(size.x*0.06), (int)(size.y*0.04), 0,(int)(size.x*0.05));
        ImageView sunrise = new ImageView(getContext());
        sunrise.setId(101);
        sunrise.setImageResource(R.drawable.sun);
        RelativeLayout.LayoutParams sunriseparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        sunriseparams.addRule(RelativeLayout.RIGHT_OF, breakfasthead.getId());
        sunriseparams.setMargins((int)(size.x*0.25), (int)(size.y*0.02), 0,(int)(size.x*0.04));
        rl.addView(breakfasthead, breakfastparams);
        rl.addView(sunrise, sunriseparams);
        breakfastlist = new NonScrollListView(getContext());
        ArrayList<FoodItem> foodlist = new ArrayList<>();
        for(int i = 0; i < mealdata.size(); i++){
            Long id = mealdata.get(i);
            Meal meal = new Meal();
            meal.populateFromDatabase(id, getContext());
            foodlist.add(new FoodItem(meal.getDishID().getFoodName(), "yummy", id));
        }
        final FoodItemAdapter adapter = new FoodItemAdapter(getContext(), R.layout.food_item, foodlist);
        breakfastlist.setAdapter(adapter);
        breakfastlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Long myitem = adapter.getItem(position).getId();
                Intent myintent = new Intent(getContext(), MealActivity.class);
                myintent.putExtra("Meal", myitem);
                startActivity(myintent);
            }
        });
        breakfastlayout.addView(breakfastlist);
        return breakfastcard;
    }
    public CardView createLunch(List<Long> mealdata){
        /*
        Formatting same as createbreakfast
        */
        // getting the meal from the list
        CardView lunchcard = new CardView(getContext());
        LinearLayout lunchlayout = new LinearLayout(getContext());
        lunchlayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int marginx = (int)(size.x*0.027);
        int marginy = (int)(size.x*0.027);
        params.setMargins(marginx, marginy, marginx, marginy);
        lunchcard.setLayoutParams(params);
        lunchcard.setRadius(0);
        lunchcard.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));
        lunchcard.setMaxCardElevation(15);
        lunchcard.setCardElevation(9);
        lunchcard.addView(lunchlayout);
        RelativeLayout rl = new RelativeLayout(getContext());
        lunchlayout.addView(rl);
        TextView lunchhead = new TextView(getContext());
        lunchhead.setText("Lunch");
        lunchhead.setId(102);
        lunchhead.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (size.x*0.085));
        RelativeLayout.LayoutParams lunchparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lunchparams.setMargins((int)(size.x*0.06), (int)(size.y*0.04), 0,(int)(size.x*0.05));
        ImageView suncomp = new ImageView(getContext());
        suncomp.setId(103);
        suncomp.setImageResource(R.drawable.fullsun);
        RelativeLayout.LayoutParams suncompparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        suncompparams.addRule(RelativeLayout.RIGHT_OF, lunchhead.getId());
        suncompparams.setMargins((int)(size.x*0.375), (int)(size.y*0.02), 0,(int)(size.x*0.04));
        rl.addView(lunchhead, lunchparams);
        rl.addView(suncomp, suncompparams);
        lunchlist = new NonScrollListView(getContext());
        ArrayList<FoodItem> foodlist = new ArrayList<>();

        // adding data from lunchlist to the cardlist
        for(int i = 0; i < mealdata.size(); i++){
            Long id = mealdata.get(i);
            Meal meal = new Meal();
            meal.populateFromDatabase(id, getContext());
            foodlist.add(new FoodItem(meal.getDishID().getFoodName(), "yummy", id));
        }
        //foodlist.add(new FoodItem("thosai", "yummy"));
        //foodlist.add(new FoodItem("thosai", "yummy"));
        //foodlist.add(new FoodItem("aloo paratha", "not so good"));
        final FoodItemAdapter adapter = new FoodItemAdapter(getContext(), R.layout.food_item, foodlist);
        lunchlist.setAdapter(adapter);
        lunchlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Long myitem = adapter.getItem(position).getId();
                Intent myintent = new Intent(getContext(), MealActivity.class);
                myintent.putExtra("Meal", myitem);
                startActivity(myintent);
            }
        });
        lunchlayout.addView(lunchlist);
        return lunchcard;
    }
    public CardView createDinner(List<Long> mealdata){
        /*
        Formatting same as createbreakfast
        */
        CardView dinnercard = new CardView(getContext());
        LinearLayout dinnerlayout = new LinearLayout(getContext());
        dinnerlayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int marginx = (int)(size.x*0.027);
        int marginy = (int)(size.x*0.027);
        params.setMargins(marginx, marginy, marginx, marginy);
        dinnercard.setLayoutParams(params);
        dinnercard.setRadius(0);
        dinnercard.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));
        dinnercard.setMaxCardElevation(15);
        dinnercard.setCardElevation(9);
        dinnercard.addView(dinnerlayout);
        RelativeLayout rl = new RelativeLayout(getContext());
        dinnerlayout.addView(rl);
        TextView dinnerhead = new TextView(getContext());
        dinnerhead.setText("Dinner");
        dinnerhead.setId(104);
        dinnerhead.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (size.x*0.085));
        RelativeLayout.LayoutParams dinnerparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dinnerparams.setMargins((int)(size.x*0.06), (int)(size.y*0.04), 0,(int)(size.x*0.05));
        ImageView moon = new ImageView(getContext());
        moon.setId(105);
        moon.setImageResource(R.drawable.moon);
        RelativeLayout.LayoutParams moonparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        moonparams.addRule(RelativeLayout.RIGHT_OF, dinnerhead.getId());
        moonparams.setMargins((int)(size.x*0.365), (int)(size.y*0.02), 0,(int)(size.x*0.04));
        rl.addView(dinnerhead, dinnerparams);
        rl.addView(moon, moonparams);
        dinnerlist = new NonScrollListView(getContext());
        ArrayList<FoodItem> foodlist = new ArrayList<>();
        for(int i = 0; i < mealdata.size(); i++){
            Long id = mealdata.get(i);
            Meal meal = new Meal();
            meal.populateFromDatabase(id, getContext());
            foodlist.add(new FoodItem(meal.getDishID().getFoodName(), "yummy", id));
        }
        final FoodItemAdapter adapter = new FoodItemAdapter(getContext(), R.layout.food_item, foodlist);
        dinnerlist.setAdapter(adapter);
        dinnerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Long myitem = adapter.getItem(position).getId();
                Intent myintent = new Intent(getContext(), MealActivity.class);
                myintent.putExtra("Meal", myitem);
                startActivity(myintent);
            }
        });
        dinnerlayout.addView(dinnerlist);
        return dinnercard;
    }

}
