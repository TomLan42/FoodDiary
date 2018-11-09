package com.example.internadmin.fooddiary.Activities

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.internadmin.fooddiary.R
import android.content.Intent.getIntent
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.StrictMode
import android.widget.TextView
import android.widget.Toast
import com.example.internadmin.fooddiary.Models.Meal
import com.example.internadmin.fooddiary.R.id.imageViewShare
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_share.*
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt
import android.graphics.Bitmap
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import kotlin.math.pow


class ShareActivity : AppCompatActivity() {

    private lateinit var mymeal: Meal
    val  SHARE_REQUEST_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        //Receive meal ID and Calories info
        val intent = getIntent()
        val mealID = intent.getLongExtra("Meal", -1)
        val mealCalories = intent.getFloatExtra("Calories",0.toFloat()).roundToInt()

        //Load meal image from class Meal
        mymeal = Meal()
        mymeal.populateFromDatabase(mealID, this)
        val foodImage = mymeal.foodImg


        //Display meal image
        val matrix = Matrix()
        matrix.postRotate(90f)
        val bitmap = Bitmap.createBitmap(foodImage, 0, 0, foodImage.width, foodImage.height, matrix, true)
        var newbitmap: Bitmap= drawTextToBitmap(bitmap,180,mealCalories.toString()+" Cal")
        imageViewShare.setImageBitmap(newbitmap)

        shareButton.setOnClickListener{

            val file = File (externalCacheDir, "myImage.png")
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG,90,fOut)
            fOut.flush()
            fOut.close()
            file.setReadable(true,false)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            shareIntent.putExtra (Intent.EXTRA_STREAM, Uri.fromFile(file))
            shareIntent.type = "image/png"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,"subject")
            startActivityForResult(Intent.createChooser(shareIntent,"Share the image via"),SHARE_REQUEST_CODE)


        }
        //Display caloreis information
        //textViewCalories.setText(mealCalories.toString()+" Cal")

        filterButton1.setOnClickListener {
            //make copy of bitmap
            Toasty.success(this, "Applying Filter...", Toast.LENGTH_LONG).show()
            val bitmap2 = bitmap.copy(bitmap.getConfig(), true);

            //edit each pixel
            for (i in 0 until bitmap2.getWidth()) {
                for (j in 0 until bitmap2.getHeight()) {
                    val pixel = bitmap2.getPixel(i, j)
                    var red = Color.red(pixel)
                    var green = Color.green(pixel)
                    var blue = Color.blue(pixel)

                    /*modify pixel logic here*/
                    val inverseGamma = 0.667
                    var rDouble: Double = red.toDouble()
                    var gDouble: Double = green.toDouble()
                    var bDouble: Double = blue.toDouble()

                    rDouble = (rDouble/255).pow(inverseGamma) * 255
                    gDouble = (gDouble/255).pow(inverseGamma) * 255
                    bDouble = (bDouble/255).pow(inverseGamma) * 255

                    red = rDouble.roundToInt()
                    green = gDouble.roundToInt()
                    blue = bDouble.roundToInt()

                    bitmap2.setPixel(i, j, Color.argb(Color.alpha(pixel), red, green, blue))
                }
            }
            newbitmap = drawTextToBitmap(bitmap2,180,mealCalories.toString()+" Cal")
            imageViewShare.setImageBitmap(newbitmap)
            Toasty.success(this, "Filter Applied", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            SHARE_REQUEST_CODE-> {
                Toasty.success(this, "Meal Shared!", Toast.LENGTH_LONG).show()
            }

            else -> {
                Toast.makeText(this,"Unrecognized request code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun drawTextToBitmap(bitmap: Bitmap,textSize: Int = 180, text1: String): Bitmap {


        var bitmapConfig = bitmap.config;
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one


        val canvas = Canvas(bitmap)
        // new antialised Paint
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.rgb(248, 131, 121)
        // text size in pixels
        paint.textSize = textSize.toFloat()
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)

        // draw text to the Canvas center
        val bounds = Rect()
        //draw the first text
        paint.getTextBounds(text1, 0, text1.length, bounds)
        var x = (bitmap.width - bounds.width()) / 2f
        var y = (bitmap.height + bounds.height()) / 2f - 140
        canvas.drawText(text1, x, y, paint)
        //draw trademark
        paint.textSize = 70.toFloat()
        paint.color = Color.rgb(255, 255, 255)
        paint.getTextBounds("- Powered by NTU ROSE Lab", 0, "- Powered by NTU ROSE Lab".length, bounds)
        x = (bitmap.width - bounds.width()) / 2f
        y = (bitmap.height + bounds.height()) / 2f + 500
        canvas.drawText( "- Powered by NTU ROSE Lab", x, y, paint)
        return bitmap
    }



}
