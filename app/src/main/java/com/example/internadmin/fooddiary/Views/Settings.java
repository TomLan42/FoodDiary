package com.example.internadmin.fooddiary.Views;


import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.internadmin.fooddiary.R;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Calendar;


public class Settings extends Fragment{
    SharedPreferences prefs;
    public Settings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        final SharedPreferences.Editor edit = prefs.edit();
        super.onViewCreated(view, savedInstanceState);
        final Switch calswitch = getView().findViewById(R.id.caloriesSwitch);
        final Switch carbswitch = getView().findViewById(R.id.carbsSwitch);
        Boolean calswitchstate = prefs.getBoolean(getString(R.string.trackcalories), false);
        Boolean carbswitchstate = prefs.getBoolean(getString(R.string.trackcarbs), false);
        if (calswitchstate==true){
            calswitch.setChecked(true);
        }
        else{
            calswitch.setChecked(false);
        }
        if(carbswitchstate==true){
            carbswitch.setChecked(true);
        }
        else{
            carbswitch.setChecked(false);
        }
        calswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(calswitch.isChecked()){
                    edit.putBoolean(getString(R.string.trackcalories), true);
                    edit.commit();
                }else{
                    edit.putBoolean(getString(R.string.trackcalories), false);
                    edit.commit();
                }
            }
        });
        carbswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(carbswitch.isChecked()){
                    edit.putBoolean(getString(R.string.trackcarbs), true);
                    edit.commit();
                }else{
                    edit.putBoolean(getString(R.string.trackcarbs), false);
                    edit.commit();
                }
            }
        });

    }
}
