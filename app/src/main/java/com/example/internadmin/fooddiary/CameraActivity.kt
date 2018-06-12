package com.example.internadmin.fooddiary

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
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


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
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
            takePicture()
        }

        btn_flash.setOnClickListener{
            isChecked = !isChecked

            if(isChecked){
                btn_flash.setImageResource(R.drawable.ic_flash_on)
                Toast.makeText(this, "Flash is On", Toast.LENGTH_SHORT).show()
            }else{
                btn_flash.setImageResource(R.drawable.ic_flash_off)
                Toast.makeText(this, "Flash is Off", Toast.LENGTH_SHORT).show()
            }
        }

        btn_switchcam.setOnClickListener{
            changeCamera()
        }

        permissionsGranted = permissionsDelegate.hasCameraPermission()

        if (permissionsGranted) {
            fullscreen_content.visibility = View.VISIBLE
        } else {
            permissionsDelegate.requestCameraPermission()
        }

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
                                Toast.makeText(this, "Photo captured", Toast.LENGTH_LONG).show()

                                val postTaskListener = PostTaskListener<Bundle> { result ->
                                    if (result.getString(ImageUploadTask.Result).equals(ImageUploadTask.Success)) {

                                        result.remove(ImageUploadTask.Result)

                                        try {
                                            val myfile = File.createTempFile("tempfoodimg", "jpg", this.getCacheDir())
                                            myfile.deleteOnExit()
                                            val outStream = FileOutputStream(myfile)
                                            it.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                                            outStream.close()
                                            result.putSerializable("FoodImg", myfile)
                                        } catch (e: IOException) {
                                            Log.e("Meal Caching", "Cannot create Cached Image: " + e.message)
                                        }

                                        val myintent = Intent(this, PredictionActivity::class.java)
                                        myintent.putExtras(result)
                                        startActivity(myintent)
                                        //Toast.makeText(this, "There are " + result.getInt("NoOfPredictions").toString(), Toast.LENGTH_LONG).show()

                                    } else {
                                        Toast.makeText(this, result.getString(DownloadDishIDTask.Result),
                                                Toast.LENGTH_LONG).show()

                                        val myintent = Intent(this, MainActivity::class.java)
                                        startActivity(myintent)
                                    }
                                }

                                ImageUploadTask(postTaskListener, it.bitmap, this).execute()

                            }
                            ?: Log.e(LOGGING_TAG, "Couldn't capture photo.")
                }


    }

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

    override fun onStart() {
        super.onStart()
        if (permissionsGranted) {
            fotoapparat.start()
            adjustViewsVisibility()
        }
    }

    override fun onStop() {
        super.onStop()
        if (permissionsGranted) {
            fotoapparat.stop()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissionsDelegate.resultGranted(requestCode, permissions, grantResults)) {
            permissionsGranted = true
            fotoapparat = Fotoapparat(
                    context = this,
                    view = fullscreen_content,
                    lensPosition = activeCamera.lensPosition,
                    cameraConfiguration = activeCamera.configuration,
                    cameraErrorCallback = {Log.e(LOGGING_TAG, "Camera error;", it)}
            )
            fotoapparat.start()
            adjustViewsVisibility()
            fullscreen_content.visibility = View.VISIBLE
        }
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