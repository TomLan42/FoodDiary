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
        LinearLayout breakstartlin = (getView()).findViewById(R.id.breakstartlin);
        LinearLayout breakendlin = (getView()).findViewById(R.id.breakendlin);
        LinearLayout lunchstartlin = (getView()).findViewById(R.id.lunchstartlin);
        LinearLayout lunchendlin = (getView()).findViewById(R.id.lunchendlin);
        LinearLayout dinnerstartlin = (getView()).findViewById(R.id.dinnerstartlin);
        LinearLayout dinnerendlin = (getView()).findViewById(R.id.dinnerendlin);
        final TextView breakstart = (getView()).findViewById(R.id.breakfaststarttime);
        final TextView breakend = (getView()).findViewById(R.id.breakfastendtime);
        final TextView lunchstart = (getView()).findViewById(R.id.lunchstarttime);
        final TextView lunchend = (getView()).findViewById(R.id.lunchendtime);
        final TextView dinnerstart = (getView()).findViewById(R.id.dinnerstarttime);
        final TextView dinnerend = (getView()).findViewById(R.id.dinnerendtime);

        // GET ORIGINALLY PRESENT VALUES AND SET THEM TO THE VIEW
        int gethour = prefs.getInt(getString(R.string.breakfast_start_hour), 0);
        int getmin = prefs.getInt(getString(R.string.breakfast_start_min), 0);
        breakstart.setText(String.valueOf(gethour) + ":" + String.valueOf(getmin));
        gethour = prefs.getInt(getString(R.string.breakfast_end_hour), 0);
        getmin = prefs.getInt(getString(R.string.breakfast_end_min), 0);
        breakend.setText(String.valueOf(gethour) + ":" + String.valueOf(getmin));
        gethour = prefs.getInt(getString(R.string.lunch_start_hour), 0);
        getmin = prefs.getInt(getString(R.string.lunch_start_min), 0);
        lunchstart.setText(String.valueOf(gethour) + ":" + String.valueOf(getmin));
        gethour = prefs.getInt(getString(R.string.lunch_end_hour), 0);
        getmin = prefs.getInt(getString(R.string.lunch_end_min), 0);
        lunchend.setText(String.valueOf(gethour) + ":" + String.valueOf(getmin));
        gethour = prefs.getInt(getString(R.string.dinner_start_hour), 0);
        getmin = prefs.getInt(getString(R.string.dinner_start_min), 0);
        dinnerstart.setText(String.valueOf(gethour) + ":" + String.valueOf(getmin));
        gethour = prefs.getInt(getString(R.string.dinner_end_hour), 0);
        getmin = prefs.getInt(getString(R.string.dinner_end_min), 0);
        dinnerend.setText(String.valueOf(gethour) + ":" + String.valueOf(getmin));
        //------------------------------------------------------
        breakstartlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        edit.putInt(getString(R.string.breakfast_start_hour), i);
                        edit.putInt(getString(R.string.breakfast_start_min), i1);
                        breakstart.setText(String.valueOf(i) + ":" + String.valueOf(i1));
                        edit.commit();
                    }
                }, 0, 0, true);
                timePickerDialog.show();
            }
        });
        breakendlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        edit.putInt(getString(R.string.breakfast_end_hour), i);
                        edit.putInt(getString(R.string.breakfast_end_min), i1);
                        breakend.setText(String.valueOf(i) + ":" + String.valueOf(i1));
                        edit.commit();
                    }
                }, 0, 0, true);
                timePickerDialog.show();
            }
        });
        lunchstartlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        edit.putInt(getString(R.string.lunch_start_hour), i);
                        edit.putInt(getString(R.string.lunch_start_min), i1);
                        lunchstart.setText(String.valueOf(i) + ":" + String.valueOf(i1));
                        edit.commit();
                    }
                }, 0, 0, true);
                timePickerDialog.show();
            }
        });
        lunchendlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        edit.putInt(getString(R.string.lunch_end_hour), i);
                        edit.putInt(getString(R.string.lunch_end_min), i1);
                        lunchend.setText(String.valueOf(i) + ":" + String.valueOf(i1));
                        edit.commit();
                    }
                }, 0, 0, true);
                timePickerDialog.show();
            }
        });
        dinnerstartlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        edit.putInt(getString(R.string.dinner_start_hour), i);
                        edit.putInt(getString(R.string.dinner_start_min), i1);
                        dinnerstart.setText(String.valueOf(i) + ":" + String.valueOf(i1));
                        edit.commit();
                    }
                }, 0, 0, true);
                timePickerDialog.show();
            }
        });
        dinnerendlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        edit.putInt(getString(R.string.dinner_end_hour), i);
                        edit.putInt(getString(R.string.dinner_end_min), i1);
                        dinnerend.setText(String.valueOf(i) + ":" + String.valueOf(i1));
                        edit.commit();
                    }
                }, 0, 0, true);
                timePickerDialog.show();
            }
        });
    }
}
