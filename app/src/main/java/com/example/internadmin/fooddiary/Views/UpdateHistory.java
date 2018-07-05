package com.example.internadmin.fooddiary.Views;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.internadmin.fooddiary.Activities.CameraActivity;
import com.example.internadmin.fooddiary.Activities.MealActivity;
import com.example.internadmin.fooddiary.DBHandler;
import com.example.internadmin.fooddiary.Models.FoodItem;
import com.example.internadmin.fooddiary.Models.Meal;
import com.example.internadmin.fooddiary.Models.TimePeriod;
import com.example.internadmin.fooddiary.R;
import com.example.internadmin.fooddiary.SummaryFront;
import com.example.internadmin.fooddiary.SummarySugar;
import com.example.internadmin.fooddiary.SwipeList.SwipeMenu;
import com.example.internadmin.fooddiary.SwipeList.SwipeMenuCreator;
import com.example.internadmin.fooddiary.SwipeList.SwipeMenuItem;
import com.example.internadmin.fooddiary.SwipeList.SwipeMenuListView;
import com.github.mikephil.charting.charts.BarChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class UpdateHistory extends Fragment {
    public UpdateHistory() {
        // Required empty public constructor
    }
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
    SwipeMenuListView breakfastlist;
    SwipeMenuListView lunchlist;
    LinearLayout meal_ll;
    SummaryFront caloriesfrag;
    SummarySugar sugarfrag;
    LinearLayout ll;
    SwipeMenuListView dinnerlist;
    Point size;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        ll.addView(dateselect);
        ll.addView(meal_ll);
        // GETTING THE LIST OF FOOD ITEMS IN BREAKFAST LUNCH AND DINNER
        GregorianCalendar calen = new GregorianCalendar();
        calen.setTime(new Date());
        addAllCards(calen);
        myCalendar = new GregorianCalendar();
        setDateListener();
        updateLabel();
        //sv.addView(fab);
        sv.smoothScrollTo(0, 0);
        sv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                breakfastlist.smoothCloseMenu();
                lunchlist.smoothCloseMenu();
                dinnerlist.smoothCloseMenu();
            }
        });
        return sv;
    }


    public void addAllCards(GregorianCalendar calen){
        meal_ll.removeAllViews();
        List<Long> breakfastlist = handler.getHistoryEntriesOnDay(calen.getTime(), TimePeriod.MORNING);
        List<Long> lunchlist = handler.getHistoryEntriesOnDay(calen.getTime(), TimePeriod.AFTERNOON);
        List<Long> dinnerlist = handler.getHistoryEntriesOnDay(calen.getTime(), TimePeriod.NIGHT);

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
                updateLabel();
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
    private void updateLabel(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        dateselect.setText(sdf.format(myCalendar.getTime()));
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
        breakfasthead.setId(10220);
        breakfasthead.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (size.x*0.085));
        RelativeLayout.LayoutParams breakfastparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        breakfastparams.setMargins((int)(size.x*0.06), (int)(size.y*0.04), 0,(int)(size.x*0.05));
        ImageView sunrise = new ImageView(getContext());
        sunrise.setId(10122);
        sunrise.setImageResource(R.drawable.sun);
        RelativeLayout.LayoutParams sunriseparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        sunriseparams.addRule(RelativeLayout.RIGHT_OF, breakfasthead.getId());
        sunriseparams.setMargins((int)(size.x*0.25), (int)(size.y*0.02), 0,(int)(size.x*0.04));
        rl.addView(breakfasthead, breakfastparams);
        rl.addView(sunrise, sunriseparams);

        ArrayList<FoodItem> foodlist = new ArrayList<>();
        for(int i = 0; i < mealdata.size(); i++){
            Long id = mealdata.get(i);
            Meal meal = new Meal();
            meal.populateFromDatabase(id, getContext());
            foodlist.add(new FoodItem(meal.getDishID().getFoodName(), "yummy", id));
        }
        breakfastlist = new SwipeMenuListView(getContext());
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

        //  ---------------------------------------------------- SWIPE MENU LIST VIEW
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                //deleteItem.setIcon(R.drawable.search_icon);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        breakfastlist.setMenuCreator(creator);
        breakfastlist.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        Long myitem = adapter.getItem(position).getId();
                        Intent myintent = new Intent(getContext(), MealActivity.class);
                        myintent.putExtra("Meal", myitem);
                        startActivity(myintent);
                        break;
                    case 1:
                        // delete
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
        breakfastlist.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });
        breakfastlist.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        //  ---------------------------------------------------- SWIPE MENU LIST VIEW

        breakfastlayout.addView(breakfastlist);
        LinearLayout addlayout = new LinearLayout(getContext());
        addlayout.setOrientation(LinearLayout.HORIZONTAL);
        ImageView plus = new ImageView(getContext());
        plus.setId(21212);
        //plus.setImageResource(R.drawable.add);
        plus.setPadding((int)(size.x*0.05), (int)(size.x*0.02), 0, (int)(size.x*0.02));
        addlayout.addView(plus);
        TextView additem = new TextView(getContext());
        additem.setText("Add Item");
        additem.setId(21211);
        additem.setPadding((int)(size.x*0.05), (int)(size.x*0.02), 0, (int)(size.x*0.02));
        addlayout.addView(additem);
        addlayout.setBackgroundColor(getResources().getColor(R.color.whitecolor));
        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CameraActivity.class);
                intent.putExtra("mealtime", TimePeriod.MORNING);
                intent.putExtra("mealdate", myCalendar.getTime());
                startActivity(intent);
            }
        });
        breakfastlayout.addView(addlayout);
        return breakfastcard;
    }

    public int dp2px(float dips)
    {
        return (int) (dips * getActivity().getResources().getDisplayMetrics().density + 0.5f);
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
        lunchhead.setId(1222);
        lunchhead.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (size.x*0.085));
        RelativeLayout.LayoutParams lunchparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lunchparams.setMargins((int)(size.x*0.06), (int)(size.y*0.04), 0,(int)(size.x*0.05));
        ImageView suncomp = new ImageView(getContext());
        suncomp.setId(1223);
        suncomp.setImageResource(R.drawable.fullsun);
        RelativeLayout.LayoutParams suncompparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        suncompparams.addRule(RelativeLayout.RIGHT_OF, lunchhead.getId());
        suncompparams.setMargins((int)(size.x*0.375), (int)(size.y*0.02), 0,(int)(size.x*0.04));
        rl.addView(lunchhead, lunchparams);
        rl.addView(suncomp, suncompparams);
        lunchlist = new SwipeMenuListView(getContext());
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
        //  ---------------------------------------------------- SWIPE MENU LIST VIEW
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                //deleteItem.setIcon(R.drawable.search_icon);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        lunchlist.setMenuCreator(creator);
        lunchlist.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        Long myitem = adapter.getItem(position).getId();
                        Intent myintent = new Intent(getContext(), MealActivity.class);
                        myintent.putExtra("Meal", myitem);
                        startActivity(myintent);
                        break;
                    case 1:
                        // delete
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
        lunchlist.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });
        lunchlist.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        //  ---------------------------------------------------- SWIPE MENU LIST VIEW
        LinearLayout addlayout = new LinearLayout(getContext());
        addlayout.setOrientation(LinearLayout.HORIZONTAL);
        ImageView plus = new ImageView(getContext());
        plus.setId(21112);
        //plus.setImageResource(R.drawable.add);
        plus.setPadding((int)(size.x*0.05), (int)(size.x*0.02), 0, (int)(size.x*0.02));
        addlayout.addView(plus);
        TextView additem = new TextView(getContext());
        additem.setText("Add Item");
        additem.setId(12111);
        additem.setPadding((int)(size.x*0.05), (int)(size.x*0.02), 0, (int)(size.x*0.02));
        addlayout.addView(additem);
        addlayout.setBackgroundColor(getResources().getColor(R.color.whitecolor));
        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CameraActivity.class);
                intent.putExtra("mealtime", TimePeriod.AFTERNOON);
                intent.putExtra("mealdate", myCalendar.getTime());
                startActivity(intent);
            }
        });
        lunchlayout.addView(lunchlist);
        lunchlayout.addView(addlayout);
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
        dinnerhead.setId(2104);
        dinnerhead.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (size.x*0.085));
        RelativeLayout.LayoutParams dinnerparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dinnerparams.setMargins((int)(size.x*0.06), (int)(size.y*0.04), 0,(int)(size.x*0.05));
        ImageView moon = new ImageView(getContext());
        moon.setId(2105);
        moon.setImageResource(R.drawable.moon);
        RelativeLayout.LayoutParams moonparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        moonparams.addRule(RelativeLayout.RIGHT_OF, dinnerhead.getId());
        moonparams.setMargins((int)(size.x*0.365), (int)(size.y*0.02), 0,(int)(size.x*0.04));
        rl.addView(dinnerhead, dinnerparams);
        rl.addView(moon, moonparams);
        dinnerlist = new SwipeMenuListView(getContext());
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
        //  ---------------------------------------------------- SWIPE MENU LIST VIEW
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                //deleteItem.setIcon(R.drawable.search_icon);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        dinnerlist.setMenuCreator(creator);
        dinnerlist.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        Long myitem = adapter.getItem(position).getId();
                        Intent myintent = new Intent(getContext(), MealActivity.class);
                        myintent.putExtra("Meal", myitem);
                        startActivity(myintent);
                        break;
                    case 1:
                        // delete
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
        dinnerlist.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });
        dinnerlist.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        //  ---------------------------------------------------- SWIPE MENU LIST VIEW

        dinnerlayout.addView(dinnerlist);
        LinearLayout addlayout = new LinearLayout(getContext());
        addlayout.setOrientation(LinearLayout.HORIZONTAL);
        ImageView plus = new ImageView(getContext());
        plus.setId(21112);
        //plus.setImageResource(R.drawable.add);
        plus.setPadding((int)(size.x*0.05), (int)(size.x*0.02), 0, (int)(size.x*0.02));
        addlayout.addView(plus);
        TextView additem = new TextView(getContext());
        additem.setText("Add Item");
        additem.setId(21111);
        additem.setPadding((int)(size.x*0.05), (int)(size.x*0.02), 0, (int)(size.x*0.02));
        addlayout.addView(additem);
        addlayout.setBackgroundColor(getResources().getColor(R.color.whitecolor));
        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CameraActivity.class);
                intent.putExtra("mealtime", TimePeriod.NIGHT);
                intent.putExtra("mealdate", myCalendar.getTime());
                startActivity(intent);
            }
        });
        dinnerlayout.addView(addlayout);
        return dinnercard;
    }

}
