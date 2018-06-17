package com.example.internadmin.fooddiary

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_meal.*
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.nutritionfactlabel.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*




class MealActivity : AppCompatActivity() {

    private var servingcounter = 0
    private var servingslice: Float = 0f

    private lateinit var mymeal: Meal
    private var clearCache = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)

        val totalserving: Float
        val mypizzaslicer = findViewById<miniPizzaView>(R.id.pizzaslicer)

        if(intent.hasExtra("Meal")){
            val mymealID = intent.getLongExtra("Meal", -1)
            mymeal = Meal()
            mymeal.populateFromDatabase(mymealID, this)
            totalserving = mymeal.servingAmt
            servingcounter = Math.round(totalserving - 0.5).toInt()
            servingslice = totalserving - servingcounter.toFloat()
            executeOnMealInitialized(mypizzaslicer, true)
        }else if(intent.hasExtra("DishID")){
            val mydishid = DishID(intent.getStringExtra("DishID"), intent.getIntExtra("Version", -1), this)
            totalserving = 1.8f
            mydishid.setDishIDPopulatedListener({
                servingcounter = Math.round(totalserving - 0.5).toInt()
                servingslice = totalserving - servingcounter.toFloat()
                Log.i("Serving Slice", servingslice.toString())
                mymeal = Meal(mydishid, Date(), totalserving)
                mymeal.setFoodImg(intent.getSerializableExtra("FoodImg") as File)
                executeOnMealInitialized(mypizzaslicer, false)
            })
            mydishid.execute()

        }else{
            RedirectToMainOnError("Could not receive Meal or Dish Information.", this)
        }





    }

    private fun executeOnMealInitialized(mypizzaslicer: miniPizzaView, toUpdate: Boolean){

        if(::mymeal.isInitialized){
            img_mealpic.setImageBitmap(mymeal.foodImg)

            btn_saveentry.setOnClickListener {
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

            }

            servingsviewgroup(mypizzaslicer)
            datetimeviewgroup(mymeal.timeConsumed)
            nutritionfactsviewgroup(mymeal.dishID.nutrition)
            ingredientsviewgroup(mymeal.dishID.ingredients)
        }

    }

    private fun servingsviewgroup(mypizzaslicer: miniPizzaView){

        pizzacounternumber.text = servingcounter.toString()

        text_totalservings.text = String.format("%.2f", mymeal.servingAmt)

        if(servingcounter > 0)
            pizzacounter.visibility = View.VISIBLE
        mypizzaslicer.setServingSlice(servingslice)
        //TODO: set pizza slice in meal activity

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


    }

    private fun datetimeviewgroup(defaultdate: Date){

        val dateFormat = SimpleDateFormat("E, d MMM y, hh:mm")
        setdatetime.text = dateFormat.format(defaultdate)

        setmealofday.text = getMealType(defaultdate)

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

        //TODO: check time setting from default preferences
        val breakfast: IntArray = intArrayOf(7, 30, 12, 30)
        val lunch: IntArray = intArrayOf(13, 0, 15, 30)
        val dinner: IntArray = intArrayOf(17, 45, 20, 30)

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

    private fun nutritionfactsviewgroup(nutrition: JsonObject){
        btn_expandnutritionfacts.setOnClickListener{
            nutrition_expandable.toggle()
        }

        txt_servingsizelarge.text = getString(R.string.serving_size) + ": " +
                getnutritionstr(nutrition, "Serving Size", "1 plate")

        text_servingsize.text =
                getnutritionstr(nutrition, "Serving Size", "1 plate")
        text_calories.text =
                getnutritionfloat(nutrition, "Calories", "")
        text_totalfat.text = getnutritionfloat(nutrition, "Fat", "g")
        text_satfat.text = getnutritionfloat(nutrition, "Saturated Fat", "g")
        text_transfat.text = getnutritionfloat(nutrition, "Trans Fat", "g")
        text_cholesterol.text = getnutritionfloat(nutrition, "Cholesterol", "mg")
        text_sodium.text = getnutritionfloat(nutrition, "Sodium", "mg")
        text_totalcarbohydrate.text = getnutritionfloat(nutrition, "Carbohydrate", "g")
        text_fibre.text = getnutritionfloat(nutrition, "Fibre", "g")
        text_sugars.text = getnutritionfloat(nutrition, "Sugars", "g")
        text_protein.text = getnutritionfloat(nutrition, "Protein", "g")

        text_totalfatdv.text = getdailyval(nutrition, "Fat", 78f)
        text_satfatdv.text = getdailyval(nutrition, "Saturated Fat", 20f)
        text_cholesteroldv.text = getdailyval(nutrition, "Cholesterol", 300f)
        text_sodiumdv.text = getdailyval(nutrition, "Sodium", 2300f)
        text_totalcarbohydratedv.text = getdailyval(nutrition, "Carbohydrate", 275f)
        text_fibredv.text = getdailyval(nutrition, "Fibre", 28f)

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
        var myobj: JsonElement? = nutrition.get(memberstr)
        if(myobj == null)
            return "-"
        else
            return String.format("%.2f%s",myobj!!.asFloat, unit)
    }

    private fun getdailyval(nutrition: JsonObject,
                            memberstr: String, recommendedintake: Float): String{
        var myobj: JsonElement? = nutrition.get(memberstr)
        if(myobj == null)
            return "0%"
        else
            return String.format("%.0f", myobj.asFloat/recommendedintake*100) + "%"

    }

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
