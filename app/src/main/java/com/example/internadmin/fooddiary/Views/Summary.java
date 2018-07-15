package com.example.internadmin.fooddiary.Views;


import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import com.example.internadmin.fooddiary.Barchart;
import com.example.internadmin.fooddiary.DBHandler;

import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.internadmin.fooddiary.DineCard;
import com.example.internadmin.fooddiary.Models.TimePeriod;
import com.example.internadmin.fooddiary.R;
import com.example.internadmin.fooddiary.SummaryFront;
import com.example.internadmin.fooddiary.SummarySugar;
import com.github.mikephil.charting.charts.BarChart;
import org.xmlpull.v1.XmlPullParser;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Summary extends Fragment {
    // Defining global variables required for creating the view
    // NOTE: THE WHOLE VIEW IS CREATED PROGRAMMATICALLY
    ScrollView sv;
    CardView barcard;
    DBHandler handler;
    TextView dateselect;
    Calendar myCalendar;
    int NUM_PAGES;
    ViewPager viewPager;
    TabLayout mytabdots;
    PagerAdapter pagerAdapter;
    BarChart chart;
    int cals;
    int carbs;
    LinearLayout meal_ll;
    SummaryFront caloriesfrag;
    SummarySugar sugarfrag;
    LinearLayout ll;
    Point size;
    SharedPreferences prefs;
    public Summary() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            return;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // THE MAIN SCROLLVIEW FOR THE WHOLE FRAGMENT
        sv = new ScrollView(getContext());
        // loading default preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        // get whether calories are to be tracked or not
        // trackcalories is 0 if not to be tracked and 1 if calories are to be tracked
        cals = prefs.getBoolean(getString(R.string.trackcalories), false)? 1:0;
        // trackcarbs is 0 if not to be tracked and 1 if calories are to be tracked
        carbs = prefs.getBoolean(getString(R.string.trackcarbs), false)? 1:0;
        // NUM_PAGES is required for the viewpager inside the summary fragment
        NUM_PAGES = cals + carbs + 1;
        // creating global variables for calorie and carbohydrates fragments
        caloriesfrag = new SummaryFront();
        // this aint sugar fragment.. its actually carbohydrate fragment... (though can be used for any..
        // we just have to change the "tracking" string in the global variables of the fragment)
        sugarfrag = new SummarySugar();
        // ll is the linear layout that contains all other view (since scrollview can only have one view appended to it)
        ll = new LinearLayout(getContext());
        // meal_ll is the linear layout that contains all the dining cards (Morning, Afternoon, Evening, Night)
        meal_ll = new LinearLayout(getContext());
        meal_ll.setOrientation(LinearLayout.VERTICAL);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll);
        // this is the database hanlder object
        handler = new DBHandler(getContext());
        // this is to get the screen size of the device since all the calculations are done programmatically to set
        // the layout of the view
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        // this is the textview that opens the calender dateselector
        // this is to allow the user to change user or view history
        dateselect = new TextView(getContext());
        //setting some parameters for dateselect
        dateselect.setPadding(50, 50, 50, 50);
        dateselect.setText("TextView");
        dateselect.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
        // this is the cardview for showing the circular progress bars..
        // the circular progress bars are separate fragments, they are contained in a viewpager which is contained
        // in this cardview
        CardView barcard = createProgress();
        ll.addView(barcard);
        CardView datecard = dateCard();
        ll.addView(datecard);
        ll.addView(meal_ll);
        // this calender object is used to set data according to today's date at the start of activity
        GregorianCalendar calen = new GregorianCalendar();
        calen.setTime(new Date());
        // this is the calender object that is used to change the date for which the data is shown
        myCalendar = new GregorianCalendar();
        // this is the function that adds all cards to the scrollview
        addAllCards(calen);
        // this is to set date listener (when the dateselect textview is clicked)
        setDateListener();
        // this is to update the labels in the circular progresss bars
        updateLabel(0);
        // due to listview.. scrollview has some bug and does not open in top position when activity starts
        // this smooth scroll is to move it automatically to top when activity starts
        sv.smoothScrollTo(0, 0);
        return sv;
    }
    public void addAllCards(GregorianCalendar calen){
        // we need to first remove all cards if they are present (this is useful when date is changed)
        // otherwise it will show the new date cards and also the previous date cards
        meal_ll.removeAllViews();
        // getting list of the food items consumed that day
        List<Long> breakfastlist = handler.getHistoryEntriesOnDay(calen.getTime(), TimePeriod.MORNING);
        List<Long> lunchlist = handler.getHistoryEntriesOnDay(calen.getTime(), TimePeriod.AFTERNOON);
        List<Long> dinnerlist = handler.getHistoryEntriesOnDay(calen.getTime(), TimePeriod.EVENING);
        //dinnerlist.addAll(handler.getHistoryEntriesOnDay(calen.getTime(), TimePeriod.NIGHT));

        // IF THE LIST CONTAINS NON ZERO NUMBER OF ITEMS THEN CARD FOR THAT MEAL IS CREATED (this was the idea earlier, but then removed)
        //if(breakfastlist.size() > 0){
        CardView breakfastcard = new DineCard(getContext(), size, "Morning", R.drawable.sun, breakfastlist, calen, TimePeriod.MORNING, 1000);
        meal_ll.addView(breakfastcard);
        //}
        //if(lunchlist.size() > 0){
        CardView lunchcard = new DineCard(getContext(), size, "Afternoon", R.drawable.fullsun, lunchlist, calen, TimePeriod.AFTERNOON, 1005);
        meal_ll.addView(lunchcard);
        //}
        //if(dinnerlist.size() > 0){
        CardView dinnercard = new DineCard(getContext(), size, "Night", R.drawable.moon, dinnerlist, calen, TimePeriod.EVENING, 1009);
        meal_ll.addView(dinnercard);
        //}
    }

    public CardView dateCard(){
        CardView datecard = new CardView(getContext());
        RelativeLayout ll2 = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams dateparams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams leftparams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams rightparams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ImageView left = new ImageView(getContext());
        left.setImageResource(R.drawable.leftarrow);
        ImageView right = new ImageView(getContext());
        right.setImageResource(R.drawable.rightarrow);
        left.setMaxHeight(dp2px(30));
        right.setMaxHeight(dp2px(30));
        datecard.setLayoutParams(dateparams);
        dateselect.setLayoutParams(dateparams);
        dateselect.setTextSize(dp2px((float) (5)));
        ll2.addView(left);
        dateparams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        //dateparams.setMargins(dp2px(135), 0, dp2px(135), 0);
        ll2.addView(dateselect);
        ll2.addView(right);
        left.setLayoutParams(leftparams);
        right.setLayoutParams(rightparams);
        leftparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        rightparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins((int)(size.x*0.027), 0, (int)(size.x*0.027), (int)(size.x*0.027));
        datecard.setLayoutParams(params);
        datecard.addView(ll2);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCalendar.add(Calendar.DATE, -1);
                updateLabel(1);
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCalendar.add(Calendar.DATE, 1);
                updateLabel(1);
            }
        });
        return datecard;
    }

    public void setDateListener(){
        // global calender object which is used to change the date for which data is shown
        myCalendar = new GregorianCalendar();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // set the date of mycalender to the date selected by user using dateselector
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                // update all labels (labels in circular progress bars)
                updateLabel(1);
                // all the cards (Morning, Breakfast, Dinner, Night) are added again with the new date
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
        // update the label on the circular progress bars and the dateselect textview
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        dateselect.setText(sdf.format(myCalendar.getTime()));
        if(i==1){
            caloriesfrag.updateLabel(myCalendar);
            if(carbs==1) sugarfrag.updateLabel(myCalendar);
        }
    }

    public CardView createProgress(){
        // main layout of the complete card
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        // barcard is the card at the top of the summary fragment
        barcard = new CardView(getContext());
        barcard.addView(ll);
        // addig the dateselector textview to the card
        //ll.addView(dateselect);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        // viewpager to handle transition in different circular progress bars
        viewPager = new ViewPager(getContext());
        viewPager.setId(1025);
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
        mytabdots.setId(1026);
        mytabdots.setClickable(false);
        mytabdots.setFocusable(false);


        // SETTING MARGINS TO THE CARD
        int marginx = (int)(size.x*0.027);
        int marginy = (int)(size.x*0.027);
        params.setMargins(marginx, marginy, marginx, 0);
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

        viewpagerRLParams.height = (int)(dp2px(260));
        tabdotsRLParams.height = (int)(dp2px(26));

        RelativeLayout myRL = new RelativeLayout(getContext());
        myRL.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));

        myRL.addView(viewPager, viewpagerRLParams);
        myRL.addView(mytabdots, tabdotsRLParams);
        ll.addView(myRL);

        //barcard.addView(chart);
        return barcard;
    }

    public int dp2px(float dips)
    {
        return (int) (dips * getActivity().getResources().getDisplayMetrics().density + 0.5f);
    }

    // handling the viewpager that shows circular progress bars
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // logic to show circular progress bars only for those nutrients which the user wants to keep track of
            if(NUM_PAGES == 3){
                if(position == 0) return caloriesfrag;
                if(position == 1) return sugarfrag;
                if(position == 2) return new Barchart();
                return caloriesfrag;
            }
            else if(NUM_PAGES == 2 && cals == 1){
                if(position == 0) return caloriesfrag;
                if(position == 1) return new Barchart();
                return caloriesfrag;
            }
            else{
                if(position == 0) return sugarfrag;
                if(position == 1) return new Barchart();
                return sugarfrag;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }


}
