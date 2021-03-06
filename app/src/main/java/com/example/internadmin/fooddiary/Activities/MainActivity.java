package com.example.internadmin.fooddiary.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.internadmin.fooddiary.R;
import com.example.internadmin.fooddiary.Views.MovableFloatingActionButton;
import com.example.internadmin.fooddiary.Views.Report;
import com.example.internadmin.fooddiary.Views.Settings;
import com.example.internadmin.fooddiary.Views.Summary;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class MainActivity extends AppCompatActivity{
    // Global variables in use
    int prevpos = 0;
    Summary summary;
    FragmentManager manager;
    Boolean allowRefresh = false;
    LinearLayout ll;
    AHBottomNavigation bottomNavigation;
    //Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final SharedPreferences.Editor edit = prefs.edit();
        // CODE TO GET THE SCREEN DISPLAY SIZE
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        // MAIN LAYOUT OF THE ACTIVITY, FRAMELAYOUT
        FrameLayout main = new FrameLayout(this);
        // GETTING THE ACTION BAR HEIGHT
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        getWindow().getDecorView().setBackgroundColor(getColor(R.color.mainbackground));
        // NEXT FEW LINES SET THE PROPERTIES FOR THE MOVABLE FLOATING ACTION BAR USED FOR GOING TO CAMERA ACTIVITY
        MovableFloatingActionButton fab = new MovableFloatingActionButton(MainActivity.this);
        fab.setImageResource(R.drawable.ic_camera_white_24dp);
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowRefresh = true;
                Intent camera = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(camera);
            }
        });
        fab.setX(prefs.getFloat("fabX", display.getWidth()-250f));
        fab.setY(prefs.getFloat("fabY", display.getHeight()-500f));


        // SETTING LAYOUT PARAMETERS FOR THE MAIN FRAMELAYOUT
        FrameLayout.LayoutParams fabparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        fabparams.width = (int)(size.x*0.16);
        fabparams.height = (int)(size.x*0.16);
        fab.setId(899);
        fab.setLayoutParams(fabparams);
        main.setId(894);
        main.addView(fab);

        // LINEAR LAYOUT AS A LAYOUT PLACEHOLDER FOR FRAGMENTS TO COME
        ll = new LinearLayout(MainActivity.this);


        // SETTING LAYOUT PARAMETERS FOR THE LINEAR LAYOUT
        ll.setId(895);
        ll.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams fragparams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        fragparams.setMargins(0, 0, 0, (int)(size.y*0.1));
        fragparams.height = (int)(size.y - size.y*0.14);
        fragparams.width = (int)(size.x*1);
        ll.setLayoutParams(fragparams);

        // INITIALIZING THE BOTTOM NAVIGATION BAR
        bottomNavigation = new AHBottomNavigation(MainActivity.this);


        // SETTING THE LAYOUT PARAMETERS FOR THE BOTTOM NAVIGATION BAR
        bottomNavigation.setId(1196);
        FrameLayout.LayoutParams navparams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        navparams.height = (int)((size.y)*0.1);
        bottomNavigation.setLayoutParams(navparams);
        navparams.gravity = Gravity.BOTTOM;
        main.addView(bottomNavigation);
        main.addView(ll);
        summary = new Summary();

        // FRAGMENT MANAGER
        manager = getSupportFragmentManager();
        // ignore below 3 lines unless you want to uncomment the buggy code in onStart
        // this is added because, if user uses additem from another date, i.e history, then after completion of adding the item,
        // the user should come back to the history date and not today's date
        //intent = getIntent();
        // SETTING SUMMARY AS THE INITIAL FRAGMENT
        manager.beginTransaction().replace(ll.getId(), summary).commit();


        // SETTING ITEMS FOR THE BOTTOM NAVIFATION BAR
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Summary", R.drawable.summary, R.color.colorAccent);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Settings", R.drawable.settings, R.color.colorPrimary);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Report", R.drawable.analytics, R.color.colorPrimary);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);


        // DESIGN PARAMETERS FOR THE BOTTOM NAVIGATION BAR
        bottomNavigation.setDefaultBackgroundColor(Color.WHITE);
        bottomNavigation.setAccentColor(fetchColor(R.color.colorPrimary));
        bottomNavigation.setInactiveColor(fetchColor(R.color.grey));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);

        // CREATING ON CLICK LISTENER FOR BOTTOM NAVIGATION BAR
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                FragmentTransaction transaction = manager.beginTransaction();
                // THIS IS TO CHECK THAT TRANSITION ANIMATION IS TO BE DONE FROM LEFT TO RIGHT
                // OR RIGHT TO LEFT
                if(prevpos <= position){
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                    prevpos = position;
                }else{
                    transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                    prevpos = position;
                }
                // IF THE CLICKED POSITION WAS NOT ALREADY SELECTED THEN CHANGE FRAGMENT ACCORDING
                // TO THE CLICKED POSITION
                if(!wasSelected){
                    if(position == 0){
                        Summary sum = new Summary();
                        transaction.replace(ll.getId(), sum);
                        transaction.commit();
                    }
                    if(position == 1){
                        Settings settings = new Settings();
                        transaction.replace(ll.getId(), settings);
                        transaction.commit();
                    }
                    if (position == 2){
                        Report report = new Report();
                        transaction.replace(ll.getId(), report);
                        transaction.commit();
                    }
                }
                return true;
            }
        });

        if(!prefs.contains(getString(R.string.firsttime))){
            edit.putInt(getString(R.string.firsttime), 0);
            edit.apply();
        }
        int first = prefs.getInt(getString(R.string.firsttime), 0);
        if(first==0){
            Overlay overlay = new Overlay();
            overlay.setBackgroundColor(getColor(R.color.overlay));
            Pointer pointer = new Pointer();
            pointer.setGravity(Gravity.TOP);
            TourGuide tourGuide = new TourGuide(this);
            tourGuide.setOverlay(overlay);
            tourGuide
                    .setPointer(pointer)
                    .setToolTip(new ToolTip().setTitle("Let's Get Started")
                            .setDescription("Click here to start the camera.")
                            .setGravity(Gravity.TOP)
                            .setBackgroundColor(getColor(R.color.progresscolorfillerdanger)))
                    .playOn(fab);

            //edit.putInt(getString(R.string.firsttime), 1);
            //edit.apply();
        }else if (first == 1){
            final TourGuide tourGuide = new TourGuide(this);

            Overlay overlay = new Overlay();
            overlay.setBackgroundColor(getColor(R.color.overlay));
            overlay.setHoleRadius(400);
            overlay.setHoleOffsets(0, -500);
            overlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tourGuide.cleanUp();
                    edit.putInt(getString(R.string.firsttime), -1);
                    edit.apply();
                }
            });
            tourGuide.setOverlay(overlay);
            tourGuide.setToolTip(new ToolTip().setTitle("Summary")
                                .setDescription("Your daily summary appears here. Click this to complete tutorial.")
                                .setGravity(Gravity.BOTTOM))
                    .playOn(ll);
        }

        setContentView(main);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (allowRefresh)
        {
            allowRefresh = false;
            // when activity is resumed it automatically jumps to summary fragment and sometimes
            // the bottom navbar shows highlights different fragment (the one which was open before
            //  the activity was changed originally) . To avoid that, i deliberately change everything
            // to summary fragment
            if(prevpos==0) {
                Summary summary = new Summary();
                manager.beginTransaction().replace(ll.getId(), summary).commit();
            }
        }
    }

    /*
    //WARNING: BUGGY CODE! UNCOMMENT AT YOUR OWN RISK.
    @Override
    protected void onStart() {
        super.onStart();
        if(intent.hasExtra("mealdate")){
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            GregorianCalendar cal = new GregorianCalendar();
            Date date = new Date(getIntent().getLongExtra("mealdate", 0));
            Log.d("gotedateextra", df.format(date));
            cal.setTime(date);
            summary.addAllCards(cal);
            summary.updateLabel(1);
            summary.setColors();
        }
    }*/

    // this function is to return color for a color code
    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }
}