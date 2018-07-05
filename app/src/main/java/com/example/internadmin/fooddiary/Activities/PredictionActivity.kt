package com.example.internadmin.fooddiary.Activities

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.internadmin.fooddiary.R.color.grey
import kotlinx.android.synthetic.main.activity_prediction.*
import java.io.File
import android.content.DialogInterface
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.internadmin.fooddiary.AsyncTasks.ImageUploadTask
import com.example.internadmin.fooddiary.Models.DishID
import com.example.internadmin.fooddiary.Models.Prediction
import com.example.internadmin.fooddiary.R
import com.example.internadmin.fooddiary.Testact
import com.example.internadmin.fooddiary.Views.PredictListViewAdapter
import com.example.internadmin.fooddiary.Views.Test
import com.miguelcatalan.materialsearchview.MaterialSearchView
import java.util.concurrent.TimeUnit


class PredictionActivity : AppCompatActivity() {

    private var listpredictions = ArrayList<Prediction>()
    private lateinit var  predictionlistview: ListView
    lateinit var toolbar: Toolbar
    lateinit var searchView: MaterialSearchView
    private lateinit var predictAdapter: PredictListViewAdapter
    private var mypos = -1
    lateinit var constraint: ConstraintLayout
    private lateinit var foodImgFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prediction)
        constraint = findViewById(R.id.predictionlayout)
        //var ll = LinearLayout(this)
        //val main = FrameLayout(this)
        toolbar = Toolbar(this)
        searchView = MaterialSearchView(this)
        val myactivity = this

        //// -----------------------------------------------------------
        var searchparams = RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        var toolbarparams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 150)
        searchView.layoutParams = searchparams
        searchView.id = 711
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
        toolbar.title = "FOOD!!!"
        toolbar.visibility = View.VISIBLE
        toolbar.layoutParams = toolbarparams
        toolbar.id = 710
        setSupportActionBar(toolbar)
        //ll.addView(toolbar)
        //ll.addView(constraint)
        constraint.addView(toolbar)
        constraint.addView(searchView)
        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            // WORK HERE --------------------------------------------------------------------------------------
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.closeSearch()
                //TimeUnit.SECONDS.sleep(1)
                // foodName is a function that converts a food name to internal food name
                var internalfoodname = foodName(query!!)
                val mydishid = DishID(internalfoodname, 1, myactivity)
                Log.d("FOODINTERNALNAME", internalfoodname)
                mydishid.setDishIDPopulatedListener { dataAdded ->
                    Log.d("inside here", "inside here")
                    if(dataAdded){
                        Log.d("data added", "data added")
                        val b = Bundle()
                        b.putSerializable("FoodImg", foodImgFile)
                        b.putString("DishID", internalfoodname)
                        b.putInt("Version", -1)
                        if(intent.hasExtra("mealtime")){
                            b.putSerializable("mealtime", intent.getSerializableExtra("mealtime"))
                            b.putLong("mealdate", intent.getLongExtra("mealdate", -1))
                        }
                        val intent = Intent(myactivity, MealActivity::class.java)
                        intent.putExtras(b)
                        startActivity(intent)
                    }else{
                        RedirectToMainOnError("Could not get Dish ID.", myactivity)
                    }
                }
                TimeUnit.MICROSECONDS.sleep(5)
                mydishid.execute()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        searchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
            }

            override fun onSearchViewClosed() {
            }
        })
        searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        //// -----------------------------------------------------------
        updateView()

        predictionlistview = findViewById(R.id.predictionlistview)
        predictionlistview.setChoiceMode(ListView.CHOICE_MODE_SINGLE)
        //predictionlistview.setSelector(R.color.green)

        predictAdapter = PredictListViewAdapter(this, listpredictions)

        predictionlistview.adapter = predictAdapter
        val noofitems = predictionlistview.adapter.count
        predictionlistview.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->

            btn_mealActivity.isEnabled = true
            btn_mealActivity.backgroundTintList = ColorStateList.valueOf(resources.getColor(android.R.color.holo_blue_dark))
            mypos = position

            for(i in 0 until noofitems){
                val myview = parent.getChildAt(i)
                val checkbox = myview.findViewById<CheckBox>(R.id.checkBox_predictionSelect)

                checkbox.isChecked = (i == position)
            }
        }

        //predictionlistview.setItemChecked(0, true);
        //predictionlistview.performItemClick(predictionlistview.selectedView, 0, 0)
        //val myview = predictionlistview.getChildAt(0)
        //val checkbox = myview.findViewById<CheckBox>(R.id.checkBox_predictionSelect)
        //checkbox.isChecked = true

        btn_mealActivity.setOnClickListener{
            val mypredict = predictionlistview.getItemAtPosition(mypos) as Prediction
            val mydishid = DishID(mypredict.internalFoodName, mypredict.ver, this)
            Log.d("FOODNAME", mydishid.internalFoodName)
            mydishid.setDishIDPopulatedListener { dataAdded ->
                if(dataAdded){
                    val b = Bundle()
                    b.putSerializable("FoodImg", foodImgFile)
                    b.putString("DishID", mydishid.internalFoodName)
                    b.putInt("Version", mydishid.ver)
                    if(intent.hasExtra("mealtime")){
                        val mealintent = Intent(this, MealActivity::class.java)
                        mealintent.putExtra("mealtime", intent.getSerializableExtra("mealtime"))
                        mealintent.putExtra("mealdate", intent.getLongExtra("mealdate", -1))
                        mealintent.putExtras(b)
                        startActivity(mealintent)
                    }
                    else{
                        val mealintent = Intent(this, MealActivity::class.java)
                        mealintent.putExtras(b)
                        startActivity(mealintent)
                    }
                }else{
                    RedirectToMainOnError("Could not get Dish ID.", this)
                }
            }
            mydishid.execute()
        }

    }



    private fun updateView(){
        val intent = getIntent()
        if(intent.hasExtra(ImageUploadTask.noofpredictions)){
            for(i in 0 until  intent.getIntExtra(ImageUploadTask.noofpredictions, 0)){
                listpredictions.add(intent.getSerializableExtra(i.toString()) as Prediction)
            }

            val imgview = findViewById<ImageView>(R.id.img_takenpic)
            foodImgFile = intent.getSerializableExtra("FoodImg") as File
            val imgpath = foodImgFile.absolutePath
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
    private fun foodName(foodname: String) : String{
        val strArray = foodname.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        val builder = StringBuilder()
        for (s in strArray) {
            val cap = s.substring(0, 1).toLowerCase() + s.substring(1)
            builder.append(cap + "_")
        }
        builder.setLength(builder.length-1)

        return builder.toString()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        var item = menu!!.findItem(R.id.action_search)
        searchView.setMenuItem(item)
        return true
    }

    override fun onBackPressed() {
        if(searchView.isSearchOpen){
            searchView.closeSearch()
        }
        else{
            super.onBackPressed()
        }
    }
}
