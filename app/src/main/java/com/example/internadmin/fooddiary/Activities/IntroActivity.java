package com.example.internadmin.fooddiary.Activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.widget.Toast;

import com.example.internadmin.fooddiary.R;
import com.example.internadmin.fooddiary.Views.GetMealTimeFragment;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

public class IntroActivity extends AppIntro2 {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        //addSlide(firstFragment);
        //addSlide(secondFragment);
        //addSlide(thirdFragment);
        //addSlide(fourthFragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        String title = "Welcome!";
        String description = "Please provide us some information we need to make your experience better";
        int color = Color.parseColor("#3F51B5");
        addSlide(AppIntro2Fragment.newInstance(title, description, R.drawable.ic_launcher_foreground, color));

        color = Color.parseColor("#82CAFA");
        int[] breakfasttime = {5, 0, 11, 0};
        String[] pref_breakfasttime = {getString(R.string.breakfast_start_hour),
                getString(R.string.breakfast_start_min),
                getString(R.string.breakfast_end_hour),
                getString(R.string.breakfast_end_min)};
        GetMealTimeFragment breakfast = GetMealTimeFragment.newInstance(R.drawable.sun, color,
                "Breakfast", breakfasttime, pref_breakfasttime);

        addSlide(breakfast);

        color = Color.parseColor("#4CC417");
        int[] lunchtime = {11, 0, 16, 0};
        String[] pref_lunchtime = {getString(R.string.lunch_start_hour),
                getString(R.string.lunch_start_min),
                getString(R.string.lunch_end_hour),
                getString(R.string.lunch_end_min)};
        GetMealTimeFragment lunch = GetMealTimeFragment.newInstance(R.drawable.fullsun, color,
                "Lunch", lunchtime, pref_lunchtime);

        addSlide(lunch);

        color = Color.parseColor("#C24641");
        int[] dinnertime = {17, 0, 22, 0};
        String[] pref_dinnertime = {getString(R.string.dinner_start_hour),
                getString(R.string.dinner_start_min),
                getString(R.string.dinner_end_hour),
                getString(R.string.dinner_end_min)};
        GetMealTimeFragment dinner = GetMealTimeFragment.newInstance(R.drawable.moon, color,
                "Dinner", dinnertime, pref_dinnertime);

        addSlide(dinner);

        //TODO: Add slide to check type of nutrition to track





        // OPTIONAL METHODS
        // Override bar/separator color.
        //setBarColor(color);
        //setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        //setVibrate(true);
        //setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        //TODO: Check if time range overlaps before set.

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
        edit.apply();

        finish();
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
        if(newFragment instanceof GetMealTimeFragment){
            Toast.makeText(this, "Slide changed", Toast.LENGTH_SHORT).show();
        }
    }
}