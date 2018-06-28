package com.example.internadmin.fooddiary.Views;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;

import com.example.internadmin.fooddiary.Barchart;
import com.example.internadmin.fooddiary.DBHandler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.internadmin.fooddiary.Activities.MealActivity;
import com.example.internadmin.fooddiary.Models.FoodItem;
import com.example.internadmin.fooddiary.Models.Meal;
import com.example.internadmin.fooddiary.R;
import com.example.internadmin.fooddiary.SummaryFront;
import com.example.internadmin.fooddiary.SummarySugar;
import com.example.internadmin.fooddiary.Views.FoodItemAdapter;
import com.example.internadmin.fooddiary.Views.NonScrollListView;
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

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import devlight.io.library.ArcProgressStackView;
import io.fotoapparat.preview.Frame;

public class Summary extends Fragment {
    ScrollView sv;
    CardView barcard;
    DBHandler handler;
    TextView dateselect;
    Calendar myCalendar;
    private static final int NUM_PAGES = 3;
    ViewPager viewPager;
    TabLayout mytabdots;
    PagerAdapter pagerAdapter;
    BarChart chart;
    NonScrollListView breakfastlist;
    NonScrollListView lunchlist;
    LinearLayout meal_ll;
    SummaryFront caloriesfrag;
    SummarySugar sugarfrag;
    LinearLayout ll;
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
        caloriesfrag = new SummaryFront();
        sugarfrag = new SummarySugar();
        // LINEAR LAYOUT TO CONTAIN OTHER VIEWS (SINCE SCROLLVIEW CAN HAVE ONLY ONE VIEW APPENDED IN IT)
        ll = new LinearLayout(getContext());
        meal_ll = new LinearLayout(getContext());
        meal_ll.setOrientation(LinearLayout.VERTICAL);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll);

        // DATABASE HANDLER OBJECT
        handler = new DBHandler(getContext());

        // GETTING THE SCREEN SIZE OF THE DEVICE
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        // THE ARRAY TO BE PASSED INTO THE BARCHART FUNCTION TO CREATE BAR CHART
        // FUNCTION TO CREATE BARCHART CARD
        dateselect = new TextView(getContext());
        dateselect.setPadding(50, 50, 50, 50);
        dateselect.setText("TextView");
        dateselect.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
        LinearLayout.LayoutParams dateparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dateparams.setMargins(10, 10, 10, 10);
        dateselect.setLayoutParams(dateparams);
        CardView barcard = createChart();
        ll.addView(barcard);
        ll.addView(meal_ll);
        // GETTING THE LIST OF FOOD ITEMS IN BREAKFAST LUNCH AND DINNER
        GregorianCalendar calen = new GregorianCalendar();
        calen.setTime(new Date());
        addAllCards(calen);
        myCalendar = new GregorianCalendar();
        setDateListener();
        updateLabel(0);
        //sv.addView(fab);
        sv.smoothScrollTo(0, 0);
        return sv;
    }
    public void addAllCards(GregorianCalendar calen){
        meal_ll.removeAllViews();
        List<Long> breakfastlist = handler.getHistoryEntries(getBreakFastStart(calen), getBreakFastEnd(calen));
        List<Long> lunchlist = handler.getHistoryEntries(getLunchStart(calen), getLunchEnd(calen));
        List<Long> dinnerlist = handler.getHistoryEntries(getDinnerStart(calen), getDinnerEnd(calen));

        // IF THE LIST CONTAINS NON ZERO NUMBER OF ITEMS THEN CARD FOR THAT MEAL IS CREATED
        //if(breakfastlist.size() > 0){
        CardView breakfastcard = createBreakfast(breakfastlist);
        meal_ll.addView(breakfastcard);
        //}
        //if(lunchlist.size() > 0){
        CardView lunchcard = createLunch(lunchlist);
        meal_ll.addView(lunchcard);
        //}
        //if(dinnerlist.size() > 0){
        CardView dinnercard = createDinner(dinnerlist);
        meal_ll.addView(dinnercard);
        //}
    }
    public void setDateListener(){
        myCalendar = new GregorianCalendar();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(1);
                addAllCards((GregorianCalendar) myCalendar);
            }
        };
        dateselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    private void updateLabel(int i){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        dateselect.setText(sdf.format(myCalendar.getTime()));
        if(i==1){
            caloriesfrag.updateLabel(myCalendar);
            sugarfrag.updateLabel(myCalendar);
        }
    }
    //------------------------------------------------
    //     FUNCTION TO GET BREAKFAST STARTTIME       |
    //------------------------------------------------
    public Date getBreakFastStart(GregorianCalendar mealtime){
        mealtime.set(Calendar.HOUR_OF_DAY, 0);
        mealtime.set(Calendar.MINUTE, 0);
        mealtime.set(Calendar.SECOND, 0);
        return mealtime.getTime();
    }

    //------------------------------------------------
    //     FUNCTION TO GET BREAKFAST ENDTIME       |
    //------------------------------------------------
    public Date getBreakFastEnd(GregorianCalendar mealtime){
        mealtime.set(Calendar.HOUR_OF_DAY, 11);
        mealtime.set(Calendar.MINUTE, 0);
        mealtime.set(Calendar.SECOND, 0);
        return mealtime.getTime();
    }


    //------------------------------------------------
    //     FUNCTION TO GET LUNCH STARTTIME       |
    //------------------------------------------------
    public Date getLunchStart(GregorianCalendar mealtime){
        mealtime.set(Calendar.HOUR_OF_DAY, 11);
        mealtime.set(Calendar.MINUTE, 0);
        mealtime.set(Calendar.SECOND, 0);
        return mealtime.getTime();
    }

    //------------------------------------------------
    //     FUNCTION TO GET LUNCH ENDTIME       |
    //------------------------------------------------
    public Date getLunchEnd(GregorianCalendar mealtime){
        mealtime.set(Calendar.HOUR_OF_DAY, 20);
        mealtime.set(Calendar.MINUTE, 0);
        mealtime.set(Calendar.SECOND, 0);
        return mealtime.getTime();
    }


    //------------------------------------------------
    //     FUNCTION TO GET DINNER STARTTIME       |
    //------------------------------------------------
    public Date getDinnerStart(GregorianCalendar mealtime){
        mealtime.set(Calendar.HOUR_OF_DAY, 20);
        mealtime.set(Calendar.MINUTE, 0);
        mealtime.set(Calendar.SECOND, 0);
        return mealtime.getTime();
    }


    //------------------------------------------------
    //     FUNCTION TO GET DINNER ENDTIME       |
    //------------------------------------------------
    public Date getDinnerEnd(GregorianCalendar mealtime){
        mealtime.set(Calendar.HOUR_OF_DAY, 23);
        mealtime.set(Calendar.MINUTE, 0);
        mealtime.set(Calendar.SECOND, 0);
        return mealtime.getTime();
    }


    public CardView createChart(){
        /*
        function to create the card view for the barchart and integrate barchart with it.
        Takes input a 2D array containing co-ordinates of top points of bars. (x(vals[0]), y(vals[1]))
        Returns a CardView object.
        */

        // CREATING THE CARD FOR CONTAINING BARCHART
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        barcard = new CardView(getContext());
        barcard.addView(ll);
        ll.addView(dateselect);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        viewPager = new ViewPager(getContext());
        viewPager.setId(1111);
        XmlPullParser parser = getResources().getXml(R.xml.createchartviewpager);
        try {
            parser.next();
            parser.nextTag();
        } catch (Exception e) {
            e.printStackTrace();
        }
        AttributeSet vpattr = Xml.asAttributeSet(parser);
        ViewPager.LayoutParams viewpagerparams = new ViewPager.LayoutParams(getContext(), vpattr);
        //viewpagerparams.height =
        viewPager.setLayoutParams(viewpagerparams);
        pagerAdapter = new ScreenSlidePagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setMinimumHeight(5000);

        //Adding tabdots to bottom of view.
        TabLayout.LayoutParams mytabdotsParams = new TabLayout.LayoutParams(TabLayout.LayoutParams.MATCH_PARENT
                , TabLayout.LayoutParams.WRAP_CONTENT);
        XmlPullParser tlparser = getResources().getXml(R.xml.createcharttabdots);
        try {
            tlparser.next();
            tlparser.nextTag();
        } catch (Exception e) {
            e.printStackTrace();
        }
        AttributeSet tlattr = Xml.asAttributeSet(tlparser);
        mytabdots = new TabLayout(getContext(), tlattr);
        mytabdots.setLayoutParams(mytabdotsParams);
        mytabdots.setupWithViewPager(viewPager, true);
        mytabdots.setId(1112);
        mytabdots.setClickable(false);
        mytabdots.setFocusable(false);


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

        RelativeLayout.LayoutParams tabdotsRLParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //tabdotsRLParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        tabdotsRLParams.addRule(RelativeLayout.ALIGN_BOTTOM, viewPager.getId());


        RelativeLayout.LayoutParams viewpagerRLParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        viewpagerRLParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        viewpagerRLParams.height = (int)(size.y*0.5);
        tabdotsRLParams.height = (int)(size.y*0.05);

        RelativeLayout myRL = new RelativeLayout(getContext());
        myRL.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));

        myRL.addView(viewPager, viewpagerRLParams);
        myRL.addView(mytabdots, tabdotsRLParams);
        ll.addView(myRL);

        //barcard.addView(chart);
        return barcard;
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




    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //caloriesfrag = new SummaryFront();
            if(position == 0) return caloriesfrag;
            if(position == 1) return sugarfrag;
            if(position == 2) return new Barchart();
            return caloriesfrag;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }


}
