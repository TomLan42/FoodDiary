package com.example.internadmin.fooddiary

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn_tut_activity = findViewById<Button>(R.id.btnTutorial);
        btn_tut_activity.setOnClickListener{
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent);
        }

        val btn_PieSliderDialog = findViewById<Button>(R.id.btnPieSliderDialog);
        btn_PieSliderDialog.setOnClickListener{
            val cdd = PieSliderDialog(this);
            cdd.show();
        }

        putsharedpref();

        val btn_CameraActivity = findViewById<Button>(R.id.btnCamera);
        btn_CameraActivity.setOnClickListener{
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        val btn_PredictionActivity = findViewById<Button>(R.id.btn_viewpredict);
        btn_PredictionActivity.setOnClickListener{
            val intent = Intent(this, PredictionActivity::class.java)
            startActivity(intent)
        }


        var mydishid = DishID("chapati", 1, this)
        Log.i("HALP", mydishid.nutrition.toString());



    }

    private fun putsharedpref(){
        val sharedpref = PreferenceManager.getDefaultSharedPreferences(this);
        val editor = sharedpref.edit();
        editor.putString("server_address", "http://155.69.53.72:8000/getinfo/");
        editor.apply();
    }
}