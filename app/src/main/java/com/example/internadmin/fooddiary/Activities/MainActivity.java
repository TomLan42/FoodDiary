package com.example.internadmin.fooddiary.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.v4.app.FragmentManager;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.internadmin.fooddiary.R;
import com.example.internadmin.fooddiary.Views.MovableFloatingActionButton;
import com.example.internadmin.fooddiary.Views.Report;
import com.example.internadmin.fooddiary.Views.Settings;
import com.example.internadmin.fooddiary.Views.Summary;
import com.example.internadmin.fooddiary.Views.UpdateHistory;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class MainActivity extends AppCompatActivity{
    // Global variables in use
    int prevpos = 0;
    Summary summary;
    FragmentManager manager;
    Boolean allowRefresh = false;
    LinearLayout ll;
    AHBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

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

        // SETTING SUMMARY AS THE INITIAL FRAGMENT
        manager.beginTransaction().replace(ll.getId(), summary).commit();

        // SETTING ITEMS FOR THE BOTTOM NAVIFATION BAR
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Summary", R.drawable.summary, R.color.colorAccent);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Camera", R.drawable.ic_arrow_back_white, R.color.colorPrimary);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Settings", R.drawable.settings, R.color.colorPrimary);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Report", R.drawable.analytics, R.color.colorPrimary);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);


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
                        UpdateHistory test = new UpdateHistory();
                        transaction.replace(ll.getId(), test);
                        transaction.commit();
                    }
                    if(position == 2){
                        Settings settings = new Settings();
                        transaction.replace(ll.getId(), settings);
                        transaction.commit();
                    }
                    if (position == 3){
                        Report report = new Report();
                        transaction.replace(ll.getId(), report);
                        transaction.commit();
                    }
                }
                return true;
            }
        });

        main.addView(fab);
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



    // this function is to return color for a color code
    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }
}