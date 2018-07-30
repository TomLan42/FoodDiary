package com.example.internadmin.fooddiary.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.configuration.UpdateConfiguration
import io.fotoapparat.parameter.Flash
import io.fotoapparat.result.transformer.scaled
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.app.Activity
import android.provider.MediaStore
import com.example.internadmin.fooddiary.AsyncTasks.DownloadDishIDTask
import com.example.internadmin.fooddiary.AsyncTasks.ImageUploadTask
import com.example.internadmin.fooddiary.Interfaces.PostTaskListener
import com.example.internadmin.fooddiary.R
import android.media.ExifInterface
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.view.Gravity
import es.dmoral.toasty.Toasty
import tourguide.tourguide.Overlay
import tourguide.tourguide.Pointer
import tourguide.tourguide.TourGuide


/**
 * A full-screen activity for grabbing an image from either the camera
 * or the gallery, place it into the app cache, and calls ImageUploadtask
 * to send the image to the server for prediction.
 *
 * Once the prediction is returned, the image File and the predictions are
 * passed to the PredictionActivity for the user to choose.
 */
class CameraActivity : AppCompatActivity() {
    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        fullscreen_content.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        //fullscreen_content_controls.visibility = View.VISIBLE

    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    private var activeCamera: Camera = Camera.Back
    private val permissionsDelegate = PermissionsDelegate(this)
    private var permissionsGranted: Boolean = false
    private lateinit var fotoapparat: Fotoapparat
    private var isChecked: Boolean = false
    private lateinit var tourGuide:TourGuide

    private val SELECT_IMAGE = 100

    private lateinit var imguploadtask :ImageUploadTask

    //Set all the OnClickListeners for the UI buttons
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_camera)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mVisible = true

        // Set up the user interaction to manually show or hide the system UI.
        fullscreen_content.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        btn_capture.setOnTouchListener(mDelayHideTouchListener)

        btn_capture.setOnClickListener{
            fabPCCapture.show()
            enableAllButtons(false)
            takePicture()
        }

        btn_flash.setOnClickListener{
            isChecked = !isChecked

            if(isChecked){
                btn_flash.setImageResource(R.drawable.ic_flash_on)
                Toasty.info(this, "Flash is On", Toast.LENGTH_SHORT, true).show();
            }else{
                btn_flash.setImageResource(R.drawable.ic_flash_off)
                Toasty.info(this, "Flash is Off", Toast.LENGTH_SHORT, true).show();
            }
        }

        btn_switchcam.setOnClickListener{
            changeCamera()
        }

        btn_gallery.setOnClickListener {

            fabPCGallery.show()

            val i = Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            startActivityForResult(i, SELECT_IMAGE)

        }

        btn_back.setOnClickListener{
            finish()
        }

        permissionsGranted = permissionsDelegate.hasCameraPermission()

        if (permissionsGranted) {
            startCamera()
            fullscreen_content.visibility = View.VISIBLE
        } else {
            permissionsDelegate.requestCameraPermission()
        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val firsttime = prefs.getInt(getString(R.string.firsttime), -1)


        if(firsttime == 0){
            val ptr = Pointer()
            ptr.gravity = Gravity.TOP
            val overlay = Overlay()
            overlay.backgroundColor = ContextCompat.getColor(this, R.color.overlay)
            tourGuide = TourGuide.create(this) {
                pointer { ptr }
                toolTip {
                    title { "Take Picture" }
                    description { "Snap a Food Image" }
                    gravity { Gravity.TOP }
                }
                overlay { overlay }
            }
            tourGuide.playOn(fabPCCapture)
        }

    }

    //Start the camera, and set the camera configuration
    private fun startCamera(){
        fotoapparat = Fotoapparat(
                context = this,
                view = fullscreen_content,
                lensPosition = activeCamera.lensPosition,
                cameraConfiguration = activeCamera.configuration,
                cameraErrorCallback = {Log.e(LOGGING_TAG, "Camera error;", it)}
        )

        fotoapparat.start()
        adjustViewsVisibility()

    }

    private val LOGGING_TAG = "Fotoapparat"

    //Snap an image from camera, and pass the bitmap to uploadBitmap function
    private fun takePicture(){

        if(isChecked) {
            toggleFlash(true)
        }

        val photoResult = fotoapparat
                .autoFocus()
                .takePicture()

        if(isChecked) {
            toggleFlash(false)
        }

        photoResult
                .toBitmap(scaled(scaleFactor = 0.25f))
                .whenAvailable { photo ->
                    photo
                            ?.let {
                                Log.i(LOGGING_TAG, "New photo captured. Bitmap length: ${it.bitmap.byteCount}")
                                Toasty.success(this, "Image Captured!! Fetching results", Toast.LENGTH_SHORT, true).show();

                                uploadBitmap(it.bitmap, true)


                            }
                            ?: Log.e(LOGGING_TAG, "Couldn't capture photo.")
                }


    }

    // Change from front to back camera, and vice versa
    private fun changeCamera(){
        activeCamera = when (activeCamera) {
            Camera.Front -> Camera.Back
            Camera.Back -> Camera.Front
        }

        fotoapparat.switchTo(
                lensPosition = activeCamera.lensPosition,
                cameraConfiguration = activeCamera.configuration
        )

        adjustViewsVisibility()

        Log.i(LOGGING_TAG, "New camera position: ${if (activeCamera is Camera.Back) "back" else "front"}")
    }

    // Toggle the camera flash
    private fun toggleFlash(turnon:Boolean){
        fotoapparat.updateConfiguration(
                UpdateConfiguration(
                        flashMode = if (turnon) {
                            torch()
                        } else {
                            off()
                        }
                )
        )


        //Log.i(LOGGING_TAG, "Flash is now ${if (isChecked) "on" else "off"}")
    }

    //Show the flash button if there is flash available for the camera
    private fun adjustViewsVisibility() {
        fotoapparat.getCapabilities()
                .whenAvailable { capabilities ->
                    capabilities
                            ?.let {
                                btn_flash.visibility = if (it.flashModes.contains(Flash.Torch)) View.VISIBLE else View.GONE
                            }
                            ?: Log.e(LOGGING_TAG, "Couldn't obtain capabilities.")
                }

        btn_switchcam.visibility = if (fotoapparat.isAvailable(front())) View.VISIBLE else View.GONE
    }



    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)

    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        //fullscreen_content_controls.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())

    }

    private fun show() {
        // Show the system bar
        fullscreen_content.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())

    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())

    }

    //Re-enable the interface buttons
    override fun onResume() {
        super.onResume()
        enableAllButtons(true)

    }

    //Start camera and show view
    override fun onStart() {
        super.onStart()
        if (permissionsGranted) {
            fotoapparat.start()
            adjustViewsVisibility()
        }
    }

    //Turn off camera and cancel AsyncTask, if it is running
    override fun onStop() {
        super.onStop()
        if (permissionsGranted) {
            fotoapparat.stop()
        }

        if(::imguploadtask.isInitialized && !imguploadtask.isCancelled){
            imguploadtask.cancel(true)
        }
    }

    //If permissions for camera are not granted, function that starts camera once
    //permission is granted.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissionsDelegate.resultGranted(requestCode, permissions, grantResults)) {
            permissionsGranted = true
            startCamera()
            fullscreen_content.visibility = View.VISIBLE
        }
    }

    //Override onActivityResult to get bitmap image from gallery
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {

                        val bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.data)
                        enableAllButtons(false)
                        uploadBitmap(bitmap, false)

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                fabPCGallery.hide()
                Toasty.info(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Function which takes the bitmap from either the camera or the gallery,
    // and calls AsyncTask ImageUploadTask to get the image prediction results
    // from server. Upon getting the result from server, the results and passed to
    // PredictionActivity to display to the user.
    private fun uploadBitmap(bitmap: Bitmap, isCamera: Boolean){

        val postTaskListener = PostTaskListener<Bundle> { result ->

            enableAllButtons(true)

            if (result.getString(ImageUploadTask.Result).equals(ImageUploadTask.Success)) {

                result.remove(ImageUploadTask.Result)

                if(isCamera){
                    fabPCCapture.attachListener {
                        fabPCCapture.hide()
                        goToPredictionActivity(result, bitmap)
                    }
                    fabPCCapture.beginFinalAnimation()
                } else{
                    fabPCGallery.attachListener {
                        fabPCGallery.hide()
                        goToPredictionActivity(result, bitmap)
                    }
                    fabPCGallery.beginFinalAnimation()
                }

                //Toast.makeText(this, "There are " + result.getInt("NoOfPredictions").toString(), Toast.LENGTH_LONG).show()

            } else {

                if(isCamera)
                    fabPCCapture.hide()
                else
                    fabPCGallery.hide()

                Toasty.error(this, result.getString(DownloadDishIDTask.Result),
                        Toast.LENGTH_LONG).show()

                val myintent = Intent(this, MainActivity::class.java)
                startActivity(myintent)
            }
        }

        imguploadtask = ImageUploadTask(postTaskListener, bitmap, this)
        imguploadtask.execute()
    }

    //Function which allows all buttons on the screen to be enabled/ disabled.
    fun enableAllButtons(enable :Boolean){
        btn_back.isEnabled = enable
        btn_gallery.isEnabled = enable
        btn_flash.isEnabled = enable
        btn_capture.isEnabled = enable
        btn_switchcam.isEnabled = enable
    }

    //Function which caches the image from the camera, and passes the File object,
    //along with the prediction results from the ImageUploadTask,
    //and passes it to the next activity (PredictionActivity)
    fun goToPredictionActivity(result: Bundle, bitmap: Bitmap){
        try {
            val myfile = File.createTempFile("tempfoodimg", "jpg", this.getCacheDir())
            myfile.deleteOnExit()
            val outStream = FileOutputStream(myfile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            outStream.close()
            try {
                val exifi = ExifInterface(myfile.getAbsolutePath())
                exifi.setAttribute(ExifInterface.TAG_ORIENTATION, getScreenOrientation())
                exifi.saveAttributes()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            result.putSerializable("FoodImg", myfile)
        } catch (e: IOException) {
            Log.e("Meal Caching", "Cannot create Cached Image: " + e.message)
        }
        if(intent.hasExtra("mealtime")){
            val mealtime = intent.getSerializableExtra("mealtime")
            val date = intent.getLongExtra("mealdate", -1)
            val myintent = Intent(this, PredictionActivity::class.java)
            result.putSerializable("mealtime", mealtime)
            result.putLong("mealdate", date)
            myintent.putExtras(result)
            startActivity(myintent)
        }
        else{
            val myintent = Intent(this, PredictionActivity::class.java)
            myintent.putExtras(result)
            startActivity(myintent)
        }
    }

    //Function to get the screen orientation, and return as a string
    fun getScreenOrientation(): String {
        val getOrient = windowManager.defaultDisplay
        var orientation :String
        if (getOrient.width == getOrient.height) {
            orientation = ExifInterface.ORIENTATION_NORMAL.toString()
        } else {
            if (getOrient.width < getOrient.height) {
                orientation = ExifInterface.ORIENTATION_ROTATE_270.toString()
            } else {
                orientation = ExifInterface.ORIENTATION_NORMAL.toString()
            }
        }
        return orientation
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }
}

private sealed class Camera(
        val lensPosition: LensPositionSelector,
        val configuration: CameraConfiguration
) {

    object Back : Camera(
            lensPosition = back(),
            configuration = CameraConfiguration(
                    previewResolution = firstAvailable(
                            wideRatio(highestResolution()),
                            standardRatio(highestResolution())
                    ),
                    previewFpsRange = highestFps(),
                    flashMode = off(),
                    focusMode = firstAvailable(
                            continuousFocusPicture(),
                            autoFocus()
                    ),
                    frameProcessor = {
                        // Do something with the preview frame
                    }
            )
    )

    object Front : Camera(
            lensPosition = front(),
            configuration = CameraConfiguration(
                    previewResolution = firstAvailable(
                            wideRatio(highestResolution()),
                            standardRatio(highestResolution())
                    ),
                    previewFpsRange = highestFps(),
                    flashMode = off(),
                    focusMode = firstAvailable(
                            fixed(),
                            autoFocus()
                    )
            )
    )
}