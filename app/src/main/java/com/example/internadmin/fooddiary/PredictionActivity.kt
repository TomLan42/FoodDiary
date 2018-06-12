package com.example.internadmin.fooddiary

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import com.example.internadmin.fooddiary.R.color.grey
import kotlinx.android.synthetic.main.activity_prediction.*
import java.io.File
import android.content.DialogInterface
import android.os.Build
import android.support.v7.app.AlertDialog


class PredictionActivity : AppCompatActivity() {

    private var listpredictions = ArrayList<Prediction>()
    private lateinit var  predictionlistview: ListView
    private lateinit var predictAdapter: PredictListViewAdapter
    private var mypos = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prediction)


        predictionlistview = findViewById(R.id.predictionlistview)
        predictionlistview.setChoiceMode(ListView.CHOICE_MODE_SINGLE)
        predictionlistview.setSelector(R.color.grey)


        updateView()

        predictAdapter = PredictListViewAdapter(this, listpredictions)

        predictionlistview.adapter = predictAdapter
        predictionlistview.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->

            btn_mealActivity.isEnabled = true
            btn_mealActivity.backgroundTintList = ColorStateList.valueOf(resources.getColor(android.R.color.holo_blue_dark))
            mypos = position
            Toast.makeText(this, "Found as " + position.toString(), Toast.LENGTH_LONG).show()
        }

        btn_mealActivity.setOnClickListener{
            val mypredict = predictionlistview.getItemAtPosition(mypos) as Prediction
            Toast.makeText(this, mypredict.foodName, Toast.LENGTH_LONG).show()
        }

    }


    private fun updateView(){
        val intent = getIntent()
        if(intent.hasExtra(ImageUploadTask.noofpredictions)){
            for(i in 0 until  intent.getIntExtra(ImageUploadTask.noofpredictions, 0)){
                listpredictions.add(intent.getSerializableExtra(i.toString()) as Prediction)
            }

            val imgview = findViewById<ImageView>(R.id.img_takenpic)
            val imgpath = (intent.getSerializableExtra("FoodImg") as File).absolutePath
            imgview.setImageBitmap(BitmapFactory.decodeFile(imgpath))

            btn_mealActivity.isEnabled = false
            btn_mealActivity.backgroundTintList = ColorStateList.valueOf(resources.getColor(grey))

        }else{
            RedirectToMainOnError("Could not Retrieve Predictions", this)
        }
    }

    private fun RedirectToMainOnError(Message: String, ctx: Context){
        val builder  = AlertDialog.Builder(ctx)
        builder.setCancelable(false)
        builder.setTitle("Error")
                .setMessage(Message)
                .setNegativeButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                    val myintent = Intent(ctx, MainActivity::class.java)
                    startActivity(myintent)
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }


}
