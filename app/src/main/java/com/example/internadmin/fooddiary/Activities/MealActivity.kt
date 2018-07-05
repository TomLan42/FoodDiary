package com.example.internadmin.fooddiary.Activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.example.internadmin.fooddiary.Interfaces.ServingSliceListener
import com.example.internadmin.fooddiary.Models.DishID
import com.example.internadmin.fooddiary.Models.Meal
import com.example.internadmin.fooddiary.Models.TimePeriod
import com.example.internadmin.fooddiary.R
import com.example.internadmin.fooddiary.Views.PieSliderDialog
import com.example.internadmin.fooddiary.Views.miniPizzaView
import kotlinx.android.synthetic.main.activity_meal.*
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.nutritionfactlabel.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*




class MealActivity : AppCompatActivity() {

    //private var servingcounter = 0
    //private var servingslice: Float = 0f

    private lateinit var mymeal: Meal
    private var clearCache = false
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)

        var totalserving: Float
        //val mypizzaslicer = findViewById<miniPizzaView>(R.id.pizzaslicer)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        if(intent.hasExtra("Meal")){
            val mymealID = intent.getLongExtra("Meal", -1)
            mymeal = Meal()
            mymeal.populateFromDatabase(mymealID, this)
            totalserving = mymeal.servingAmt
            //servingcounter = Math.round(totalserving - 0.5).toInt()
            //servingslice = totalserving - servingcounter.toFloat()
            executeOnMealInitialized(true)
        }else if(intent.hasExtra("DishID")){
            val mydishid = DishID(intent.getStringExtra("DishID"), intent.getIntExtra("Version", -1), this)



            mydishid.setDishIDPopulatedListener{
                totalserving = getPrevServingAmt(mydishid)
                //servingcounter = Math.round(totalserving - 0.5).toInt()
                //servingslice = totalserving - servingcounter.toFloat()
                //Log.i("Serving Slice", servingslice.toString())

                if(intent.hasExtra("mealtime")){
                    Log.i("HELLLOOO", "WASSSUPPP")
                    val mealdate = Date(intent.getLongExtra("mealdate", -1))
                    val mealtime = intent.getSerializableExtra("mealtime") as? TimePeriod
                    mymeal = Meal(mydishid, mealdate, mealtime, totalserving)
                }else{
                    Log.i("BYEEEE", "WASSSUPPP")
                    mymeal = Meal(mydishid, Date(), totalserving, this)
                }

                val myfoodimg = intent.getSerializableExtra("FoodImg")
                if(myfoodimg != null){
                    mymeal.setFoodImg(myfoodimg as File)
                }

                executeOnMealInitialized(false)
            }
            mydishid.execute()

        }else{
            RedirectToMainOnError("Could not receive Meal or Dish Information.", this)
        }





    }

    private fun executeOnMealInitialized(toUpdate: Boolean){

        if(::mymeal.isInitialized){
            img_mealpic.setImageBitmap(mymeal.foodImg)

            btn_saveentry.setOnClickListener {

                setDefaultServingAmt(mymeal)

                if(toUpdate){
                    mymeal.updateInDatabase(this)
                    Toast.makeText(this, "Updated Entry!", Toast.LENGTH_LONG).show()
                }else{
                    mymeal.saveToDatabase(this)
                    clearCache = !mymeal.deleteFoodImg()
                    //deleteFoodImg() in here is to delete the cached Food image.
                    //Copied Image in DBhandler not deleted.
                    //If deleteFoodImg() is unsuccessful, returns false. Set to clear Cache.
                    Toast.makeText(this, "Saved Entry!", Toast.LENGTH_LONG).show()
                }

                val myintent = Intent(this, MainActivity::class.java)
                startActivity(myintent)
            }

            btn_deleteMeal.setOnClickListener{
                if(mymeal.deleteMeal(this)){
                    Toast.makeText(this, "Entry Deleted.", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this, "Could not delete Entry.", Toast.LENGTH_LONG).show()
                }

                val myintent = Intent(this, MainActivity::class.java)
                startActivity(myintent)
                finish()

            }

            btn_moreinfo.setOnClickListener{

                val alertadd = AlertDialog.Builder(this)
                val factory = LayoutInflater.from(this)
                val view = factory.inflate(R.layout.mealactivityinfo_dialog, null)

                //val dishimg = view.findViewById<ImageView>(R.id.dialog_imageview)
                //dishimg.setImageBitmap(mymeal.dishID.getFoodImg())
                nutritionfactsviewgroup(mymeal.dishID.nutrition, view)
                alertadd.setView(view)
                alertadd.setTitle(mymeal.dishID.getFoodName())
                alertadd.setNegativeButton("Ok") { dlg, _ -> dlg.dismiss() }
                alertadd.show()

            }

            //servingsviewgroup(mypizzaslicer)
            servingsviewgroup(mymeal.servingAmt)
            datetimeviewgroup(mymeal.timeConsumed)
            //nutritionfactsviewgroup(mymeal.dishID.nutrition)
            //ingredientsviewgroup(mymeal.dishID.ingredients)

            txt_servingsizelarge.text = getString(R.string.serving_size) + ": " +
                    getnutritionstr(mymeal.dishID.nutrition, "Serving Size", "1 plate")
        }else{
            RedirectToMainOnError("Meal was not properly initialized.", this)
        }

    }

    private fun getPrevServingAmt(mydishid: DishID):Float{
        val servingamt = prefs.getFloat(mydishid.internalFoodName, -1f)

        if(servingamt < 0){
            return getdefaultservings(mydishid.nutrition)
        }
        return servingamt
    }

    private fun getdefaultservings(nutrition: JsonObject): Float{
        val myobj: JsonElement? = nutrition.get("Default Serving")
        if(myobj == null)
            return 1.5f
        else
            return myobj.asFloat
    }

    private fun setDefaultServingAmt(mymeal: Meal){
        val FoodName = mymeal.dishID.internalFoodName
        val defaultserving = mymeal.servingAmt

        val edit = prefs.edit()
        edit.putFloat(FoodName, defaultserving)
        edit.apply()
    }

    /*private fun servingsviewgroup(mypizzaslicer: miniPizzaView){

        pizzacounternumber.text = servingcounter.toString()

        text_totalservings.text = String.format("%.2f", mymeal.servingAmt)

        if(servingcounter > 0)
            pizzacounter.visibility = View.VISIBLE
        mypizzaslicer.setServingSlice(servingslice)

        btn_pluspizzacount.setOnClickListener{
            servingcounter++
            pizzacounternumber.text = servingcounter.toString()
            if(servingcounter > 0){
                pizzacounter.visibility = View.VISIBLE
            }
            mymeal.servingAmt = servingcounter + servingslice
            text_totalservings.text = String.format("%.2f", mymeal.servingAmt)
        }

        btn_minuspizzacount.setOnClickListener{
            servingcounter--
            if(servingcounter <= 0) {
                servingcounter = 0
                pizzacounter.visibility = View.GONE
            }
            pizzacounternumber.text = servingcounter.toString()
            mymeal.servingAmt = servingcounter + servingslice
            text_totalservings.text = String.format("%.2f", mymeal.servingAmt)
        }

        view_servings.setOnClickListener{
            val cdd = PieSliderDialog(this, servingslice, ServingSliceListener {
                servingslice = it
                mypizzaslicer.setServingSlice(servingslice)
                mymeal.servingAmt = servingcounter + servingslice
                text_totalservings.text = String.format("%.2f", mymeal.servingAmt)
            })
            cdd.show()
        }


    }*/

    private fun servingsviewgroup(servingAmt: Float) {

        seekbar_servingsize.setMax(30)
        if(servingAmt > 3)
            seekbar_servingsize.setProgress(30)
        else
            seekbar_servingsize.setProgress(Math.round(servingAmt*10))
        seekbar_servingsize.incrementProgressBy(1)

        edittxt_servingsize.setText(String.format("%.2f", servingAmt))

        val myseekbar_servingsize: SeekBar = findViewById(R.id.seekbar_servingsize)

        myseekbar_servingsize.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                if(fromUser){
                    mymeal.servingAmt = progress/10f
                    edittxt_servingsize.setText(String.format("%.2f", mymeal.servingAmt))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {


            }

        })

        edittxt_servingsize.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val mystr = edittxt_servingsize.text.toString()

                if(!TextUtils.isEmpty(mystr)) {
                    mymeal.servingAmt = mystr.toFloat()

                    if(mymeal.servingAmt > 3)
                        myseekbar_servingsize.setProgress(30)
                    else
                        myseekbar_servingsize.setProgress((mymeal.servingAmt*10).toInt())
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        /*

        edittxt_servingsize.setOnFocusChangeListener{ _ , hasFocus ->

            if(!hasFocus){
                val mystr = edittxt_servingsize.text.toString()

                if(TextUtils.isEmpty(mystr)) {
                    mymeal.servingAmt = 1f
                    myseekbar_servingsize.setProgress(10)
                    edittxt_servingsize.setText("1.0")
                }
            }

        }*/


    }

    private fun datetimeviewgroup(defaultdate: Date){

        val dateFormat = SimpleDateFormat("E, d MMM y")

        setmealofday.text = getMealType(defaultdate)
        setdatetime.text = dateFormat.format(mymeal.timeConsumed)

        btn_datetimepicker.setOnClickListener {
            SingleDateAndTimePickerDialog.Builder(this)
                    .mainColor(resources.getColor(R.color.colorPrimary))
                    .title("Set Meal Time")
                    .defaultDate(defaultdate)
                    .listener { date ->
                        mymeal.timeConsumed = date
                        setdatetime.text = dateFormat.format(mymeal.timeConsumed)
                        setmealofday.text = getMealType(mymeal.timeConsumed)
                    }.display()
        }
    }

    private fun getMealType(mydate: Date): String{
        val calendar = GregorianCalendar.getInstance()
        calendar.time = mydate
        val mytime = intArrayOf(calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE))

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val breakfast: IntArray = intArrayOf(
                prefs.getInt(getString(R.string.breakfast_start_hour), 0),
                prefs.getInt(getString(R.string.breakfast_start_min), 0),
                prefs.getInt(getString(R.string.breakfast_end_hour), 12),
                prefs.getInt(getString(R.string.breakfast_end_min), 0))
        val lunch: IntArray = intArrayOf(
                prefs.getInt(getString(R.string.lunch_start_hour), 13),
                prefs.getInt(getString(R.string.lunch_start_min), 0),
                prefs.getInt(getString(R.string.lunch_end_hour), 16),
                prefs.getInt(getString(R.string.lunch_end_min), 0))
        val dinner: IntArray = intArrayOf(
                prefs.getInt(getString(R.string.dinner_start_hour), 17),
                prefs.getInt(getString(R.string.dinner_start_min), 45),
                prefs.getInt(getString(R.string.dinner_end_hour), 20),
                prefs.getInt(getString(R.string.dinner_end_min), 30))

        if(inTimeRange(mytime, breakfast))
            return "Breakfast"
        else if(inTimeRange(mytime, lunch))
            return "Lunch"
        else if (inTimeRange(mytime, dinner))
            return "Dinner"
        else
            return "Snack"
    }

    private fun inTimeRange(giventime: IntArray, timerange: IntArray): Boolean{

        if(timerange[0] > timerange[2])
            timerange[2] += 24

        if((timerange[0] < giventime[0]) && (giventime[0] < timerange[2]))
            return true
        else if (timerange[0] == giventime[0]){
            if(timerange[1] <= giventime[1])
                return true
            return false
        }else if (timerange[2] == giventime[0]){
            if(timerange[3] >= giventime[1])
                return true
            return false
        }
        return false

    }

    private fun nutritionfactsviewgroup(nutrition: JsonObject, view: View){
        /*
        btn_expandnutritionfacts.setOnClickListener{
            nutrition_expandable.toggle()
        }

        txt_servingsizelarge.text = getString(R.string.serving_size) + ": " +
                getnutritionstr(nutrition, "Serving Size", "1 plate")*/

        val text_servingsize = view.findViewById<TextView>(R.id.text_servingsize)
        val text_calories = view.findViewById<TextView>(R.id.text_calories)
        val text_totalfat = view.findViewById<TextView>(R.id.text_totalfat)
        val text_satfat = view.findViewById<TextView>(R.id.text_satfat)
        val text_transfat = view.findViewById<TextView>(R.id.text_transfat)
        val text_cholesterol = view.findViewById<TextView>(R.id.text_cholesterol)
        val text_sodium = view.findViewById<TextView>(R.id.text_sodium)
        val text_totalcarbohydrate = view.findViewById<TextView>(R.id.text_totalcarbohydrate)
        val text_fibre = view.findViewById<TextView>(R.id.text_fibre)
        val text_sugars = view.findViewById<TextView>(R.id.text_sugars)
        val text_protein = view.findViewById<TextView>(R.id.text_protein)
        val text_totalfatdv = view.findViewById<TextView>(R.id.text_totalfatdv)
        val text_satfatdv = view.findViewById<TextView>(R.id.text_satfatdv)
        val text_cholesteroldv = view.findViewById<TextView>(R.id.text_cholesteroldv)
        val text_sodiumdv = view.findViewById<TextView>(R.id.text_sodiumdv)
        val text_totalcarbohydratedv = view.findViewById<TextView>(R.id.text_totalcarbohydratedv)
        val text_fibredv = view.findViewById<TextView>(R.id.text_fibredv)

        text_servingsize.setText(getnutritionstr(nutrition, "Serving Size", "1 plate"))
        text_calories.setText(
                getnutritionfloat(nutrition, "Energy", ""))
        text_totalfat.setText( getnutritionfloat(nutrition, "Fat", "g"))
        text_satfat.setText( getnutritionfloat(nutrition, "Saturated Fat", "g"))
        text_transfat.setText( getnutritionfloat(nutrition, "Trans Fat", "g"))
        text_cholesterol.setText( getnutritionfloat(nutrition, "Cholesterol", "mg"))
        text_sodium.setText( getnutritionfloat(nutrition, "Sodium", "mg"))
        text_totalcarbohydrate.setText( getnutritionfloat(nutrition, "Carbohydrate", "g"))
        text_fibre.setText( getnutritionfloat(nutrition, "Fibre", "g"))
        text_sugars.setText( getnutritionfloat(nutrition, "Sugars", "g"))
        text_protein.setText( getnutritionfloat(nutrition, "Protein", "g"))

        text_totalfatdv.setText( getdailyval(nutrition, "Fat", 78f))
        text_satfatdv.setText( getdailyval(nutrition, "Saturated Fat", 20f))
        text_cholesteroldv.setText( getdailyval(nutrition, "Cholesterol", 300f))
        text_sodiumdv.setText( getdailyval(nutrition, "Sodium", 2300f))
        text_totalcarbohydratedv.setText( getdailyval(nutrition, "Carbohydrate", 275f))
        text_fibredv.setText( getdailyval(nutrition, "Fibre", 28f))

    }

    private fun getnutritionstr(nutrition: JsonObject,
                                memberstr: String, default: String): String{
       val myobj: JsonElement? = nutrition.get(memberstr)
       if(myobj == null)
            return default
       else
            return myobj.asString
    }

    private fun getnutritionfloat(nutrition: JsonObject,
                                  memberstr: String, unit: String): String{
        val myobj: JsonElement? = nutrition.get(memberstr)
        if(myobj == null)
            return "-"
        else
            return String.format("%.2f%s",myobj!!.asFloat, unit)
    }

    private fun getdailyval(nutrition: JsonObject,
                            memberstr: String, recommendedintake: Float): String{
        val myobj: JsonElement? = nutrition.get(memberstr)
        if(myobj == null)
            return "0%"
        else
            return String.format("%.0f", myobj.asFloat/recommendedintake*100) + "%"

    }

    /*
    private fun ingredientsviewgroup(ingredients: List<String>){
        btn_expandingredients.setOnClickListener {
            ingredients_expandable.toggle()
        }

        val mystring = StringBuilder()
        val myseparator = ", "

        for(ingredient in ingredients){
            mystring.append(myseparator).append(ingredient)
        }

        var finalstring = mystring.substring(0, mystring.length- 2).toString()

        if(finalstring.isEmpty())
            finalstring = "No Description"

        text_ingredients.text = finalstring

    }*/

    private fun RedirectToMainOnError(Message: String, ctx: Context){
        val builder  = AlertDialog.Builder(ctx)
        builder.setCancelable(false)
        builder.setTitle("Error")
                .setMessage(Message)
                .setNegativeButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                    val myintent = Intent(ctx, MainActivity::class.java)
                    startActivity(myintent)
                    finish()
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(clearCache){
            try {
                trimCache(applicationContext) //if trimCache is static
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    fun trimCache(context: Context) {
        try {
            val dir = context.cacheDir
            if (dir != null && dir.isDirectory) {
                deleteDir(dir)
                Log.i("Cache", "Cache Deleted")
            }
        } catch (e: Exception) {
            // TODO: handle exception
        }

    }

    fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children!!.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }

        // The directory is now empty so delete it
        return dir!!.delete()
    }

}
