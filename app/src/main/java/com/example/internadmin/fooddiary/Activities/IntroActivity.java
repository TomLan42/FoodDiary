package com.example.internadmin.fooddiary.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.widget.Toast;

import com.example.internadmin.fooddiary.R;
import com.example.internadmin.fooddiary.Views.GetDefaultServingFragment;
import com.example.internadmin.fooddiary.Views.GetNutritionTrackFragment;
import com.example.internadmin.fooddiary.Views.GetUserProfileFragment;
import com.example.internadmin.fooddiary.Views.MoreInfoFragment;
import com.example.internadmin.fooddiary.Views.SummaryFragment;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

/**
 * Activity which is used to set the initial preference of the user.
 *
 * Uses AppIntro2 to display the Intro Fragments.
 */

public class IntroActivity extends AppIntro2 {

    SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

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
        int color = Color.parseColor("#D36209");
        addSlide(AppIntro2Fragment.newInstance(title, description, R.drawable.ic_launcher_foreground, color));

        GetUserProfileFragment trackuserprofile = GetUserProfileFragment.newInstance(color);
        addSlide(trackuserprofile);

        GetNutritionTrackFragment tracknutrition = GetNutritionTrackFragment.newInstance(color);
        addSlide(tracknutrition);

        MoreInfoFragment getmoreinfo = MoreInfoFragment.newInstance(color);
        addSlide(getmoreinfo);

        GetDefaultServingFragment getdefaultservings = GetDefaultServingFragment.newInstance(color);
        addSlide(getdefaultservings);

        SummaryFragment summary = SummaryFragment.newInstance(color);
        addSlide(summary);


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

        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
        edit.apply();
        Toast.makeText(this, getString(R.string.settingcomplete), Toast.LENGTH_LONG).show();

        Intent myintent = new Intent(this, MainActivity.class);
        startActivity(myintent);
        finish();
        /*
        int[] breakfast = {prefs.getInt(getString(R.string.breakfast_start_hour), 0),
                prefs.getInt(getString(R.string.breakfast_start_min), 0),
                prefs.getInt(getString(R.string.breakfast_end_hour), 0),
                prefs.getInt(getString(R.string.breakfast_end_min), 0),};

        int[] lunch = {prefs.getInt(getString(R.string.lunch_start_hour), 0),
                prefs.getInt(getString(R.string.lunch_start_min), 0),
                prefs.getInt(getString(R.string.lunch_end_hour), 0),
                prefs.getInt(getString(R.string.lunch_end_min), 0),};

        int[] dinner = {prefs.getInt(getString(R.string.dinner_start_hour), 0),
                prefs.getInt(getString(R.string.dinner_start_min), 0),
                prefs.getInt(getString(R.string.dinner_end_hour), 0),
                prefs.getInt(getString(R.string.dinner_end_min), 0),};

        if (timeIntervalOverlaps(breakfast, lunch) || timeIntervalOverlaps(lunch, dinner) ||
                timeIntervalOverlaps(breakfast, dinner)) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Meal Times cannot overlap! Please select again.");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }else if(prefs.getFloat(getString(R.string.tracking_nutrition_limit), -1f) < 0f ){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Nutrition Value cannot be empty!");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        } else {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
            edit.apply();
            Toast.makeText(this, getString(R.string.settingcomplete), Toast.LENGTH_LONG).show();

            Intent myintent = new Intent(this, MainActivity.class);
            startActivity(myintent);
            finish();
        }*/

    }

    /*
    private boolean timeIntervalOverlaps(int[] timerangeA, int[] timerangeB){
        Calendar mytrAstart = Calendar.getInstance();
        Calendar mytrAend = Calendar.getInstance();
        Calendar mytrBstart = Calendar.getInstance();
        Calendar mytrBend = Calendar.getInstance();

        mytrAstart.set(Calendar.HOUR_OF_DAY, timerangeA[0]);
        mytrAstart.set(Calendar.MINUTE, timerangeA[1]);
        mytrAstart.set(Calendar.SECOND, 0);

        if(timerangeA[0] > timerangeA[2]){
            mytrAend.add(Calendar.DATE, 1);
        }
        mytrAend.set(Calendar.HOUR_OF_DAY, timerangeA[2]);
        mytrAend.set(Calendar.MINUTE, timerangeA[3]);
        mytrAend.set(Calendar.SECOND, 0);

        mytrBstart.set(Calendar.HOUR_OF_DAY, timerangeB[0]);
        mytrBstart.set(Calendar.MINUTE, timerangeB[1]);
        mytrBstart.set(Calendar.SECOND, 0);

        if(timerangeB[0] > timerangeB[2]){
            mytrBend.add(Calendar.DATE, 1);
        }
        mytrBend.set(Calendar.HOUR_OF_DAY, timerangeB[2]);
        mytrBend.set(Calendar.MINUTE, timerangeB[3]);
        mytrBend.set(Calendar.SECOND, 0);

        return (mytrAstart.getTime().getTime() <= mytrBend.getTime().getTime() &&
                mytrBstart.getTime().getTime() < mytrAend.getTime().getTime());

    }*/

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);


        // Do something when the slide changes.
        if(newFragment instanceof GetNutritionTrackFragment){
            //Toast.makeText(this, "Slide changed", Toast.LENGTH_SHORT).show();
        }
    }
}