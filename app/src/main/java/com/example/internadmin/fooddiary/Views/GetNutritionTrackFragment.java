package com.example.internadmin.fooddiary.Views;


import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.internadmin.fooddiary.R;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;
import com.github.paolorotolo.appintro.ISlideSelectionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Fragment in IntroActivity which asks the user what does he wish to use the app for.
 *
 * Allows the user to choose between weight loss, diabetes or high blood pressure(HBP may be removed).
 */

public class GetNutritionTrackFragment extends Fragment implements ISlideSelectionListener,
        ISlideBackgroundColorHolder{

    private SharedPreferences prefs;

    protected static final String ARG_BGCOLOR = "bg_color";

    private int bgColor;


    private LinearLayout mainLayout;

    CheckBox weightloss;
    CheckBox diabetes;
    //CheckBox hbpressure;

    public GetNutritionTrackFragment(){ }

    public static GetNutritionTrackFragment newInstance(int bgColor){
        GetNutritionTrackFragment fragment = new GetNutritionTrackFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_BGCOLOR, bgColor);

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if(getArguments() != null && getArguments().size() !=0){

            bgColor = getArguments().getInt(ARG_BGCOLOR);

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            bgColor = savedInstanceState.getInt(ARG_BGCOLOR);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_nutrition_track,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());


        mainLayout = view.findViewById(R.id.fragment_nutritiontrackll);
        weightloss = view.findViewById(R.id.checkBox_weightloss);
        diabetes = view.findViewById(R.id.checkBox_diabetes);
        //hbpressure = view.findViewById(R.id.checkBox_hbpressure);

        HashMap<String, CheckBox> selections = new HashMap<>();
        selections.put(getString(R.string.nut_track_weightloss), weightloss);
        selections.put(getString(R.string.nut_track_diabetes), diabetes);
        //selections.put("hbpressure", hbpressure);


        SharedPreferences.Editor edit = prefs.edit();

        if(!prefs.contains(getString(R.string.nut_track_weightloss)))
            edit.putBoolean(getString(R.string.nut_track_weightloss), true).apply();

        setCheckboxesListener(selections, edit);

        mainLayout.setBackgroundColor(bgColor);

        Boolean wl = prefs.getBoolean(getString(R.string.nut_track_weightloss), true);
        Boolean db = prefs.getBoolean(getString(R.string.nut_track_diabetes), false);
        //Boolean hbp = prefs.getBoolean("hbpressure", false);

        weightloss.setChecked(wl);
        diabetes.setChecked(db);
        //hbpressure.setChecked(hbp);


    }

    private Boolean atLeastOneChecked(List<CheckBox> Checkboxes){

        for (int i = 0; i< Checkboxes.size(); i++){
            if(Checkboxes.get(i).isChecked())
                return true;
        }

        return false;
    }

    //Method which forces the user to choose at least one checkbox among the options.
    //Updates the checkbox and the preferences when the option is selected/deselected.
    private void setCheckboxesListener(final HashMap<String, CheckBox> Checkboxes,
                                       final SharedPreferences.Editor edit){

        for (Map.Entry<String, CheckBox> entry : Checkboxes.entrySet()){

            final String mykey = entry.getKey();

            entry.getValue().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(!isChecked && !atLeastOneChecked(
                            new ArrayList<CheckBox>(Checkboxes.values()))) {
                        buttonView.setChecked(true);
                        isChecked = true;
                    }

                    edit.putBoolean(mykey, isChecked);
                    edit.apply();

                }
            });

        }
    }


    @Override
    public void onSlideSelected() {

        Boolean wl = prefs.getBoolean(getString(R.string.nut_track_weightloss), true);
        Boolean db = prefs.getBoolean(getString(R.string.nut_track_diabetes), false);
        //Boolean hbp = prefs.getBoolean("hbpressure", false);

        weightloss.setChecked(wl);
        diabetes.setChecked(db);
        //hbpressure.setChecked(hbp);

    }

    @Override
    public void onSlideDeselected() {
        /*
        Boolean wl = prefs.getBoolean(getString(R.string.nut_track_weightloss), false);
        Boolean db = prefs.getBoolean(getString(R.string.nut_track_diabetes), false);
        Boolean hbp = prefs.getBoolean("hbpressure", false);

        Toast.makeText(getContext(), "Weight Loss: " + bool2txt(wl)+
        "\n Diabetes: " + bool2txt(db) +
        "\n HBP: " + bool2txt(hbp), Toast.LENGTH_LONG).show();
        */

    }

    /*
    private String bool2txt(Boolean isTrue){
        if(isTrue)
            return "True";
        return "False";
    }*/

    @Override
    public int getDefaultBackgroundColor(){
        return bgColor;
    }

    @Override
    public void setBackgroundColor(@ColorInt int backgroundColor){
        mainLayout.setBackgroundColor(backgroundColor);
    }


}
