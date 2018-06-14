package com.example.internadmin.fooddiary;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        //addSlide(firstFragment);
        //addSlide(secondFragment);
        //addSlide(thirdFragment);
        //addSlide(fourthFragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        String title = "Hello World!";
        String description = "Please provide us some information we need to make your experience better";
        int color = Color.parseColor("#3F51B5");
        addSlide(AppIntroFragment.newInstance(title, description, R.drawable.ic_launcher_foreground, color));
        String title2 = "Hello Again!";
        String description2 = "This is another sample slide";
        addSlide(AppIntroFragment.newInstance(title2, description2, R.drawable.ic_launcher_foreground, color));
        IntroHello hello = new IntroHello();
        addSlide(hello);
        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(color);
        //setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        //setVibrate(true);
        //setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
        if(newFragment instanceof IntroHello){
            Toast.makeText(this, "Slide changed", Toast.LENGTH_SHORT).show();
        }
    }
}