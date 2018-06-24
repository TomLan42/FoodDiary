package com.example.internadmin.fooddiary.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.internadmin.fooddiary.R
import android.content.Intent
import android.os.Handler
import android.preference.PreferenceManager
import android.view.WindowManager
import android.os.Build
import android.support.v4.content.ContextCompat


class SplashActivity : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH:Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)
        }


        val prefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false)

        if (previouslyStarted) {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }else{


            Handler().postDelayed(Runnable {

                // working with the app intro
                // THE NEXT FEW LINES SET A SHARED PREFERENCE IF IT IS NOT ALREADY DONE, SO IF IT IS NOT ALREADY DONE THEN INTRO
                // ACTIVITY NEEDS TO BE SHOWN (SINCE APP IS OPENED FOR THE FIRST TIME)

                    val edit = prefs.edit()
                    //edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);

                    edit.putInt(getString(R.string.breakfast_start_hour), 0)
                    edit.putInt(getString(R.string.breakfast_start_min), 0)
                    edit.putInt(getString(R.string.breakfast_end_hour), 11)
                    edit.putInt(getString(R.string.breakfast_end_min), 0)
                    edit.putInt(getString(R.string.lunch_start_hour), 11)
                    edit.putInt(getString(R.string.lunch_start_min), 0)
                    edit.putInt(getString(R.string.lunch_end_hour), 17)
                    edit.putInt(getString(R.string.lunch_end_min), 0)
                    edit.putInt(getString(R.string.dinner_start_hour), 18)
                    edit.putInt(getString(R.string.dinner_start_min), 0)
                    edit.putInt(getString(R.string.dinner_end_hour), 23)
                    edit.putInt(getString(R.string.dinner_end_min), 59)
                    edit.apply()
                    val intro = Intent(this, IntroActivity::class.java)
                    startActivity(intro)
                    finish()
                }
            , SPLASH_DISPLAY_LENGTH)
        }


    }
}
