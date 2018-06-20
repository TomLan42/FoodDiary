package com.example.internadmin.fooddiary;


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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;
import com.github.paolorotolo.appintro.ISlideSelectionListener;

public class GetMealTimeFragment extends Fragment implements ISlideSelectionListener,
        ISlideBackgroundColorHolder{

    private TextView mealname;
    private ImageView meal;
    private TextView starttime;
    private TextView endtime;
    private ImageButton btn_starttime;
    private ImageButton btn_endtime;
    private SharedPreferences prefs;

    protected static final String ARG_DRAWABLE = "drawable";
    protected static final String ARG_MEALNAME = "meal_name";
    protected static final String ARG_STARTHR = "start_hour";
    protected static final String ARG_STARTMIN = "start_min";
    protected static final String ARG_ENDHR = "end_hour";
    protected static final String ARG_ENDMIN = "end_min";
    protected static final String ARG_BGCOLOR = "bg_color";

    protected static final String ARG_PREFSTARTHR = "pref_start_hour";
    protected static final String ARG_PREFSTARTMIN = "pref_start_min";
    protected static final String ARG_PREFENDHR = "pref_end_hour";
    protected static final String ARG_PREFENDMIN = "pref_end_min";

    private int drawable, starthr, endhr,
            startmin, endmin, bgColor;

    private String pref_starthr, pref_startmin, pref_endhr, pref_endmin;
    private String MealName;

    private LinearLayout mainLayout;

    public GetMealTimeFragment(){ }

    public static GetMealTimeFragment newInstance(int drawable, int bgColor, String MealName,
                                       int[] defaults, String[] prefnames){
        GetMealTimeFragment fragment = new GetMealTimeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DRAWABLE, drawable);
        args.putInt(ARG_BGCOLOR, bgColor);
        args.putString(ARG_MEALNAME, MealName);
        args.putInt(ARG_STARTHR, defaults[0]);
        args.putInt(ARG_STARTMIN, defaults[1]);
        args.putInt(ARG_ENDHR, defaults[2]);
        args.putInt(ARG_ENDMIN, defaults[3]);

        args.putString(ARG_PREFSTARTHR, prefnames[0]);
        args.putString(ARG_PREFSTARTMIN, prefnames[1]);
        args.putString(ARG_PREFENDHR, prefnames[2]);
        args.putString(ARG_PREFENDMIN, prefnames[3]);

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if(getArguments() != null && getArguments().size() !=0){
            drawable = getArguments().getInt(ARG_DRAWABLE);
            bgColor = getArguments().getInt(ARG_BGCOLOR);
            MealName = getArguments().getString(ARG_MEALNAME);
            starthr = getArguments().getInt(ARG_STARTHR);
            startmin = getArguments().getInt(ARG_STARTMIN);
            endhr = getArguments().getInt(ARG_ENDHR);
            endmin = getArguments().getInt(ARG_ENDMIN);

            pref_starthr = getArguments().getString(ARG_PREFSTARTHR);
            pref_startmin = getArguments().getString(ARG_PREFSTARTMIN);
            pref_endhr = getArguments().getString(ARG_PREFENDHR);
            pref_endmin = getArguments().getString(ARG_PREFENDMIN);

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            drawable = savedInstanceState.getInt(ARG_DRAWABLE);
            bgColor = savedInstanceState.getInt(ARG_BGCOLOR);
            MealName = savedInstanceState.getString(ARG_MEALNAME);
            starthr = savedInstanceState.getInt(ARG_STARTHR);
            startmin = savedInstanceState.getInt(ARG_STARTMIN);
            endhr = savedInstanceState.getInt(ARG_ENDHR);
            endmin = savedInstanceState.getInt(ARG_ENDMIN);

            pref_starthr = savedInstanceState.getString(ARG_PREFSTARTHR);
            pref_startmin = savedInstanceState.getString(ARG_PREFSTARTMIN);
            pref_endhr = savedInstanceState.getString(ARG_PREFENDHR);
            pref_endmin = savedInstanceState.getString(ARG_PREFENDMIN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_getmealtime, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());


        mainLayout = view.findViewById(R.id.fragmentll);

        meal = view.findViewById(R.id.meal);
        mealname = view.findViewById(R.id.mealname);
        starttime = view.findViewById(R.id.starttime);
        endtime = view.findViewById(R.id.endtime);
        btn_starttime = view.findViewById(R.id.btn_starttime);
        btn_endtime = view.findViewById(R.id.btn_endtime);

        mealname.setText(MealName);
        meal.setImageResource(drawable);
        starttime.setText(int2time(starthr, startmin));
        endtime.setText(int2time(endhr, endmin));

        btn_starttime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        starthr = hour;
                        startmin = min;
                        starttime.setText(int2time(starthr, startmin));
                    }
                }, starthr, startmin, true);
                timePickerDialog.show();
            }
        });

        btn_endtime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        endhr = hour;
                        endmin = min;
                        endtime.setText(int2time(endhr, endmin));
                    }
                }, endhr, endmin, true);
                timePickerDialog.show();
            }
        });

        mainLayout.setBackgroundColor(bgColor);

    }

    private String int2time(int hr, int min){
        if(hr > 12){
            return String.format("%02d:%02d p.m.", hr-12, min);
        }else if(hr == 12){
            return String.format("%02d:%02d p.m.", hr, min);
        }else if(hr == 0){
            return "12:" + Integer.toString(min) + " a.m.";
        }else{
            return String.format("%02d:%02d a.m.", hr, min);
        }
    }

    @Override
    public void onSlideSelected() {

    }

    @Override
    public void onSlideDeselected() {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(pref_starthr, starthr);
        edit.putInt(pref_startmin, startmin);
        edit.putInt(pref_endhr, endhr);
        edit.putInt(pref_endmin, endmin);
        edit.apply();

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
