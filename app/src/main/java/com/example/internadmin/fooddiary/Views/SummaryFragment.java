package com.example.internadmin.fooddiary.Views;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class SummaryFragment extends Fragment implements ISlideSelectionListener,
        ISlideBackgroundColorHolder{

    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    protected static final String ARG_BGCOLOR = "bg_color";

    private int bgColor;

    private Boolean wl;
    private Boolean hbp;
    private Boolean db;

    private int DailyCalorieLimit;

    private LinearLayout mainLayout;
    private LinearLayout wtlossLayout;
    private LinearLayout HBPLayout;
    private LinearLayout DiabetesLayout;

    private TextView textView_wtloss;
    private TextView wtloss_lackinfowarning;

    private TextView textView_hbp;
    private TextView diabetes_customwarning;

    private TextView textView_sugar;
    private TextView hbp_customwarning;

    public SummaryFragment(){ }

    public static SummaryFragment newInstance(int bgColor){
        SummaryFragment fragment = new SummaryFragment();
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
        return inflater.inflate(R.layout.fragment_summary,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        edit = prefs.edit();


        mainLayout = view.findViewById(R.id.fragment_summaryll);
        wtlossLayout = view.findViewById(R.id.summary_wtloss);
        HBPLayout = view.findViewById(R.id.summary_hbp);
        DiabetesLayout = view.findViewById(R.id.summary_sugar);

        textView_wtloss = view.findViewById(R.id.textView_wtloss);
        wtloss_lackinfowarning = view.findViewById(R.id.wtloss_lackinfowarning);

        textView_sugar = view.findViewById(R.id.textView_sugar);
        diabetes_customwarning = view.findViewById(R.id.diabetes_customwarning);

        textView_hbp = view.findViewById(R.id.textView_hbp);
        hbp_customwarning = view.findViewById(R.id.hbp_customwarning);


        mainLayout.setBackgroundColor(bgColor);

    }


    @Override
    public void onSlideSelected() {
        wl = prefs.getBoolean("weightloss", false);
        db = prefs.getBoolean("diabetes", false);
        hbp = prefs.getBoolean("hbpressure", false);

        if(wl){
            wtlossLayout.setVisibility(View.VISIBLE);
            wtloss_lackinfowarning.setVisibility(View.GONE);

            int BMR;

            if(prefs.contains("Sex") && prefs.contains("Height")
                    && prefs.contains("Age") && prefs.contains("Weight")){
                int height = prefs.getInt("Height", 160);
                int age = prefs.getInt("Age", 30);
                int currentwt = prefs.getInt("Weight", 60);
                BMR = (int) Math.round(10 * currentwt + 6.25 * height - 5 * age);
                switch(prefs.getInt("Sex", 0)){
                    case 0:
                        BMR +=5;
                        break;
                    case 1:
                        BMR -= 161;
                        break;
                    default:
                        break;
                }
            }else{
                BMR = 2000;
                wtloss_lackinfowarning.setVisibility(View.VISIBLE);
            }

            String wtlossdisplay;

            if(prefs.contains("DesiredWeight") && prefs.contains("DesiredWeightWks")
                    && prefs.contains("Weight")){
                int desiredwt = prefs.getInt("DesiredWeight", 50);
                int currentwt = prefs.getInt("Weight", 60);
                int desiredwtwks = prefs.getInt("DesiredWeightWks", 10);
                DailyCalorieLimit = (int) Math.round(BMR - (currentwt - desiredwt)/desiredwtwks/0.45*500);

                wtlossdisplay = "Target: Lose " + String.valueOf(currentwt-desiredwt) + " kg (from "
                        + String.valueOf(currentwt) + " kg to " + String.valueOf(desiredwt) + " kg ) in " +
                        String.valueOf(desiredwtwks) + " weeks.\n";
                wtlossdisplay += "Daily Calorie Limit: " + String.valueOf(DailyCalorieLimit) + " kcal";
            }else{
                wtlossdisplay = "Daily Calorie Limit: " + String.valueOf(BMR) + " kcal";
                DailyCalorieLimit = BMR;
                wtloss_lackinfowarning.setVisibility(View.VISIBLE);
            }

            edit.putInt(getString(R.string.tracking_nutrition_limit), DailyCalorieLimit).apply();

            textView_wtloss.setText(wtlossdisplay);

        }
        else {
            wtlossLayout.setVisibility(View.GONE);
        }

        if(db){
            DiabetesLayout.setVisibility(View.VISIBLE);
            diabetes_customwarning.setVisibility(View.GONE);
            int diabetesregime = prefs.getInt("DiabetesRegime", 0);
            switch (diabetesregime){
                case 0:
                    textView_sugar.setText("Chosen Option:" + getString(R.string.reccarbsregime));
                    break;
                case 1:

                    if(prefs.contains("CustomDiabetesRegime")){
                        textView_sugar.setText("Chosen Option: Custom Regime ("
                        + String.valueOf(prefs.getInt("CustomDiabetesRegime", 45))
                        + " g)");
                    }else{
                        diabetes_customwarning.setVisibility(View.VISIBLE);
                        edit.putInt("CustomDiabetesRegime", 45).apply();
                        textView_sugar.setText("Chosen Option: Custom Regime (default 45g used)");
                    }
                    break;

            }
        } else{
            DiabetesLayout.setVisibility(View.GONE);
        }


        if(hbp){
            HBPLayout.setVisibility(View.VISIBLE);
            hbp_customwarning.setVisibility(View.GONE);
            int hbpregime = prefs.getInt("HBPRegime", 0);
            switch (hbpregime){
                case 0:
                    textView_hbp.setText("Chosen Option:" + getString(R.string.stdsaltregime));
                    break;
                case 1:
                    textView_hbp.setText("Chosen Option:" + getString(R.string.recsaltregime));
                case 2:

                    if(prefs.contains("CustomHBPRegime")){
                        textView_hbp.setText("Chosen Option: Custom Regime ("
                                + String.valueOf(prefs.getInt("CustomHBPRegime", 1500))
                                + " mg)");
                    }else{
                        hbp_customwarning.setVisibility(View.VISIBLE);
                        edit.putInt("CustomHBPRegime", 1500).apply();
                        textView_hbp.setText("Chosen Option: Custom Regime (default 1500mg used)");
                    }
                    break;

            }
        } else{
            HBPLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onSlideDeselected() {


    }


    @Override
    public int getDefaultBackgroundColor(){
        return bgColor;
    }

    @Override
    public void setBackgroundColor(@ColorInt int backgroundColor){
        mainLayout.setBackgroundColor(backgroundColor);
    }


}
