package com.example.internadmin.fooddiary.Activities

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_prediction.*
import java.io.File
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Matrix
import android.preference.PreferenceManager
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.*
import com.example.internadmin.fooddiary.AsyncTasks.FormSubmitTask
import com.example.internadmin.fooddiary.AsyncTasks.ImageUploadTask
import com.example.internadmin.fooddiary.Models.DishID
import com.example.internadmin.fooddiary.Models.Prediction
import com.example.internadmin.fooddiary.R
import com.example.internadmin.fooddiary.Views.PredictListViewAdapter
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.prediction_footer_layout.*
import tourguide.tourguide.Overlay
import tourguide.tourguide.Pointer
import tourguide.tourguide.TourGuide

/**
 * PredictionActivity takes the predictions and images
 * produced by CameraActivity and displays them.
 *
 * Once the user chooses the prediction, it passes the image
 * and the prediction to the MealActivity.
 */

class PredictionActivity : AppCompatActivity() {

    private var listpredictions = ArrayList<Prediction>()
    private lateinit var  predictionlistview: ListView
    lateinit var toolbar: Toolbar
    lateinit var formdata: EditText
    lateinit var formsubmit: Button
    lateinit var searchView: MaterialSearchView
    private lateinit var predictAdapter: PredictListViewAdapter
    private var mypos = 0
    lateinit var filename: String
    lateinit var constraint: ConstraintLayout
    private lateinit var foodImgFile: File
    private lateinit var tourGuide:TourGuide

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prediction)
        constraint = findViewById(R.id.predictionlayout)
        //var ll = LinearLayout(this)
        //val main = FrameLayout(this)
        toolbar = Toolbar(this)
        searchView = MaterialSearchView(this)

        //// -----------------------------------------------------------
        var searchparams = RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        var toolbarparams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 150)
        searchView.layoutParams = searchparams
        searchView.id = 711
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
        toolbar.title = "Predictions"
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
                var submitter = FormSubmitTask(filename, query, this@PredictionActivity)
                submitter.execute()
                var internalfoodname = foodName(query!!)
                val mydishid = DishID(internalfoodname, 1, this@PredictionActivity)
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
                        val myintent = Intent(this@PredictionActivity, MealActivity::class.java)
                        myintent.putExtras(b)
                        startActivity(myintent)
                    }else{
                        RedirectToMainOnError("Could not get Dish ID.", this@PredictionActivity)
                    }
                }
                //TimeUnit.MICROSECONDS.sleep(5)
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

        //populate listview with prediction items
        predictionlistview = findViewById(R.id.predictionlistview)
        predictionlistview.setChoiceMode(ListView.CHOICE_MODE_SINGLE)

        val footerView = (this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.prediction_footer_layout, null, false)
        predictionlistview.addFooterView(footerView)

        predictAdapter = PredictListViewAdapter(this, listpredictions)

        predictionlistview.adapter = predictAdapter
        val noofitems = predictionlistview.adapter.count
        //When prediction item is clicked, it is saved into int mypos
        predictionlistview.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->

            mypos = position

            for(i in 0 until noofitems-1){
                val myview = parent.getChildAt(i)
                val checkbox = myview.findViewById<CheckBox>(R.id.checkBox_predictionSelect)

                checkbox.isChecked = (i == position)
            }

            if(::tourGuide.isInitialized){
                tourGuide.apply {
                    cleanUp()
                    toolTip{
                        title { "Next Page" }
                        description { "Click on Button to Proceed." }
                        gravity { Gravity.TOP }
                    }
                    overlay {
                        style { Overlay.Style.CIRCLE }
                    }
                }.playOn(btn_mealActivity)
            }
        }

        footer_layout.setOnClickListener {

            searchView.showSearch()

        }

        //When btn_mealActivity is clicked, mypos is used to find the correct prediction
        //item. Next, the Food Image and DishID is passed to MealActivity.java.
        btn_mealActivity.setOnClickListener{
            val mypredict = predictionlistview.getItemAtPosition(mypos) as Prediction
            val mydishid = DishID(mypredict.foodName, mypredict.ver, this)
            Log.d("FOODNAME", mydishid.foodName)
            if (mypos > 0){
                FormSubmitTask(filename, mypredict.foodName, this@PredictionActivity).execute()
            }
            mydishid.setDishIDPopulatedListener { dataAdded ->
                if(dataAdded){
                    val b = Bundle()
                    b.putSerializable("FoodImg", foodImgFile)
                    b.putString("DishID", mydishid.foodName)
                    b.putInt("Version", mydishid.ver)
                    if(intent.hasExtra("mealtime")){
                        val mealintent = Intent(this, MealActivity::class.java)
                        b.putSerializable("mealtime", intent.getSerializableExtra("mealtime"))
                        b.putLong("mealdate", intent.getLongExtra("mealdate", -1))
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

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val firsttime = prefs.getInt(getString(R.string.firsttime), -1)


        if(firsttime == 0){
            tourGuide = TourGuide.create(this) {
                toolTip {
                    title { "Select Prediction" }
                    description { "Select the correct Dish." }
                    gravity { Gravity.TOP }
                }
                overlay {
                    backgroundColor { ContextCompat.getColor(this@PredictionActivity, R.color.overlay) }
                    style { Overlay.Style.ROUNDED_RECTANGLE }
                }
            }
            tourGuide.playOn(predictionlistview)

        }

    }


    //Function which sets the image and the predictions in the activity UI
    private fun updateView(){
        val intent = getIntent()
        if(intent.hasExtra(ImageUploadTask.noofpredictions)){
            filename = intent.getStringExtra(ImageUploadTask.filename)
            Log.d("INSIDE PREDICTION", filename)
            for(i in 0 until  intent.getIntExtra(ImageUploadTask.noofpredictions, 0)){
                listpredictions.add(intent.getSerializableExtra(i.toString()) as Prediction)
            }

            val imgview = findViewById<ImageView>(R.id.img_takenpic)
            foodImgFile = intent.getSerializableExtra("FoodImg") as File
            val imgpath = foodImgFile.absolutePath

            val myimg = BitmapFactory.decodeFile(imgpath)

            val matrix = Matrix()
            matrix.postRotate(90f)
            imgview.setImageBitmap(Bitmap.createBitmap(myimg, 0, 0,
                    myimg.width, myimg.height, matrix, true))

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

    //Convert foodName to internal FoodName
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
