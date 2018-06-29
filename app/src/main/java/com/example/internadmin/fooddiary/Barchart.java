package com.example.internadmin.fooddiary;


import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.internadmin.fooddiary.Models.DishID;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Barchart extends Fragment {
    DBHandler handler;
    Point size;
    BarChart chart;
    public Barchart() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        handler = new DBHandler(getContext());
        LinearLayout ll = new LinearLayout(getContext());
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        float[] val = getcaloriearray();
        BarChart chart = getchart(val);
        ll.addView(chart);
        return ll;
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
    public BarChart getchart(float[] val){
        chart = new BarChart(getContext());
        final ArrayList<String> xLabel = getXAxisValues();
        // ARRAY LIST OF DATASET POINTS
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        for(int i = 0; i < 7; i++){
            yVals.add(new BarEntry(i+1, val[i]));
        }
        BarDataSet set1 = new BarDataSet(yVals, "label");
        set1.setColor(R.color.grey);
        dataSets.add(set1);
        BarData data = new BarData(dataSets);


        // SETTING CHART PARAMETERS SO THAT AXIS AND GRID AND LABELS ARE NOT VISIBLE
        chart.setData(data);
        chart.setMinimumHeight((int) (size.y*0.4));
        chart.animateY(1000);
        //chart.getXAxis().setEnabled(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getLegend().setEnabled(false);
        //chart.centerViewTo(1, 1);
        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(2500);
        LimitLine l = new LimitLine(2200, "Max calories (2200)");
        leftAxis.addLimitLine(l);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setAxisMinimum(0);
        leftAxis.setDrawLabels(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int index = (int)value;
                return xLabel.get(index);
            }
        });
        rightAxis.setEnabled(false);
        //leftAxis.setEnabled(false);
        chart.setDrawBorders(false);
        chart.setTouchEnabled(false);
        chart.setMinimumWidth((int) (size.x*0.96));
        // SETTING DESCRIPTION TO NULL
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        return chart;
    }
    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Sun");
        xAxis.add("Sun");
        xAxis.add("Mon");
        xAxis.add("Tue");
        xAxis.add("Wed");
        xAxis.add("Thu");
        xAxis.add("Fri");
        xAxis.add("Sat");
        return xAxis;
    }
    public float[] getcaloriearray(){
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        float[] val = {0, 0, 0, 0, 0, 0, 0};
        Calendar today = new GregorianCalendar();
        today.setTime(new Date());
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            Calendar tom = new GregorianCalendar();
            tom = (Calendar) cal.clone();
            tom.set(Calendar.HOUR_OF_DAY, 23);
            tom.set(Calendar.MINUTE, 59);
            tom.set(Calendar.SECOND, 59);
            HashMap<String, Float> servings = handler.getAllServingsTimePeriod(cal.getTime(), tom.getTime());
            Iterator it = servings.entrySet().iterator();
            float tot = 0;
            while(it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                DishID id = new DishID((String) pair.getKey(), -1, getContext());
                id.execute();
                JsonObject nutrition = id.getNutrition();
                float calorie = nutrition.get("Energy").getAsFloat();
                tot += calorie*(float)pair.getValue();
            }
            val[0] = tot;
        }
        else{
            cal.add( Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK)-1));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            Calendar tom;
            int counter = 0;
            while (true){
                tom = (Calendar) cal.clone();
                tom.set(Calendar.HOUR_OF_DAY, 23);
                tom.set(Calendar.MINUTE, 59);
                tom.set(Calendar.SECOND, 59);
                HashMap<String, Float> servings = handler.getAllServingsTimePeriod(cal.getTime(), tom.getTime());
                Iterator it = servings.entrySet().iterator();
                float tot = 0;
                while(it.hasNext()){
                    Map.Entry pair = (Map.Entry)it.next();
                    DishID id = new DishID((String) pair.getKey(), -1, getContext());
                    id.execute();
                    JsonObject nutrition = id.getNutrition();
                    Log.d("jsoncheck", nutrition.toString());
                    float calorie = nutrition.get("Energy").getAsFloat();
                    tot += calorie*(float)pair.getValue();
                }
                val[counter] = tot;
                counter += 1;
                //Log.d("Time solver", format.format(cal.getTime()));
                //Log.d("Time solver 2", format.format(today.getTime()));
                if(isSameDay(cal, today)) break;
                cal.add(Calendar.DATE, 1);
            }
        }
        return val;
    }


}
