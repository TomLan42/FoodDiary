package com.example.internadmin.fooddiary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.design.widget.FloatingActionButton;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.File;

public class MainActivity extends AppCompatActivity{
    MaterialSearchView searchView;
    Toolbar toolbar;
    int prevpos = 0;
    Summary summary;
    FragmentManager manager;
    Boolean allowRefresh = false;
    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        // working with the app intro
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);
        if(!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
            edit.commit();
            Intent intro = new Intent(MainActivity.this, IntroActivity.class);
            startActivity(intro);
        }






        // main scrollview for MainActivity
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        FrameLayout main = new FrameLayout(MainActivity.this);
        // HANDLING THE TOOLBAR
        //toolbar = new Toolbar(MainActivity.this);
        //searchView = new MaterialSearchView(MainActivity.this);
        /*
        FrameLayout.LayoutParams toolparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        FrameLayout.LayoutParams searchparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        searchView.setLayoutParams(searchparams);
        searchView.setId(711);
        toolbar.setLayoutParams(toolparams);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitle("FOOD!!!");
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setId(710);
        setSupportActionBar(toolbar);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });*/
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));

        // HANDLING THE TOOLBAR
        // ACTIONBAR HEIGHT
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        //main.setOrientation(LinearLayout.VERTICAL);
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

        FrameLayout.LayoutParams fabparams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        fabparams.width = (int)(size.x*0.16);
        fabparams.height = (int)(size.x*0.16);
        fab.setId(899);
        fab.setLayoutParams(fabparams);
        main.setId(894);
        //main.addView(toolbar);
        //main.addView(searchView);
        ll = new LinearLayout(MainActivity.this);
        ll.setId(895);
        ll.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams fragparams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        fragparams.setMargins(0, 0, 0, (int)(size.y*0.1));
        fragparams.height = (int)(size.y - size.y*0.14);
        fragparams.width = (int)(size.x*1);
        ll.setLayoutParams(fragparams);
        AHBottomNavigation bottomNavigation = new AHBottomNavigation(MainActivity.this);
        bottomNavigation.setId(1196);
        FrameLayout.LayoutParams navparams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        navparams.height = (int)((size.y)*0.1);
        bottomNavigation.setLayoutParams(navparams);
        navparams.gravity = Gravity.BOTTOM;
        main.addView(bottomNavigation);
        main.addView(ll);
        summary = new Summary();
        manager = getSupportFragmentManager();
        manager.beginTransaction().replace(ll.getId(), summary).commit();
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Summary", R.drawable.ic_camera_black_24dp, R.color.colorAccent);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Camera", R.drawable.ic_arrow_back_white, R.color.colorPrimary);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Query Food", R.drawable.ic_arrow_forward_white, R.color.colorPrimary);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Query", R.drawable.ic_arrow_forward_white, R.color.colorPrimary);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
        //ONLINE CODE
        bottomNavigation.setDefaultBackgroundColor(Color.WHITE);
        bottomNavigation.setAccentColor(fetchColor(R.color.colorPrimary));
        bottomNavigation.setInactiveColor(fetchColor(R.color.grey));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                FragmentTransaction transaction = manager.beginTransaction();
                if(prevpos <= position){
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                    prevpos = position;
                }else{
                    transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                    prevpos = position;
                }
                if(!wasSelected){
                    if(position == 0){
                        transaction.replace(ll.getId(), summary);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    if(position == 1){
                        Test test = new Test();
                        transaction.replace(ll.getId(), test);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
                //manager.beginTransaction().replace(summary.getId(), test).commit();

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
            manager.beginTransaction().replace(ll.getId(), summary).commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            trimCache(getApplicationContext()); //if trimCache is static
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
                Log.i("Cache", "Cache Deleted");
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }



    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }*/
}