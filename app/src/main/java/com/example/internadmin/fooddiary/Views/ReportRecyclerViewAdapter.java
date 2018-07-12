package com.example.internadmin.fooddiary.Views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.internadmin.fooddiary.Barchart;
import com.example.internadmin.fooddiary.Models.Column;
import com.example.internadmin.fooddiary.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ReportRecyclerViewAdapter extends RecyclerView.Adapter {

        private ArrayList<Bundle> mDataset;
        private TabLayout.OnTabSelectedListener changePeriodListener;
        private AdapterView.OnItemSelectedListener changeTimeListener;
        private ArrayList<String> changeTimeSpinnerStrings;
        private ArrayAdapter<String> adapter;

        public ReportRecyclerViewAdapter(ArrayList<Bundle> myDataset,
                                         TabLayout.OnTabSelectedListener changePeriodListener,
                                         AdapterView.OnItemSelectedListener changeTimeListener,
                                         Context ctx) {
            this.mDataset = myDataset;
            this.changePeriodListener = changePeriodListener;
            this.changeTimeListener = changeTimeListener;
            this.changeTimeSpinnerStrings = new ArrayList<>();
            this.adapter = new ArrayAdapter<>(ctx,
                    android.R.layout.simple_spinner_dropdown_item, changeTimeSpinnerStrings);
            this.adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        class HeaderViewHolder extends ReportRecyclerView.ViewHolder {
            TextView headerCardTitle;
            TextView headerCardContent;
            TabLayout headerCardPeriodSelect;
            Spinner headerCardTimeSelect;

            HeaderViewHolder(View itemView){
                super(itemView);

                headerCardTitle = itemView.findViewById(R.id.txt_headercardTitle);
                headerCardContent = itemView.findViewById(R.id.txt_headercardContent);
                headerCardPeriodSelect = itemView.findViewById(R.id.periodtabs);
                headerCardTimeSelect = itemView.findViewById(R.id.spinner_time);

                headerCardPeriodSelect.addTab(headerCardPeriodSelect.newTab().setText("Daily"));
                headerCardPeriodSelect.addTab(headerCardPeriodSelect.newTab().setText("Weekly"));
                headerCardPeriodSelect.addTab(headerCardPeriodSelect.newTab().setText("Monthly"));

                headerCardPeriodSelect.addOnTabSelectedListener(changePeriodListener);
                headerCardTimeSelect.setOnItemSelectedListener(changeTimeListener);

            }
        }

        class CongratViewHolder extends  ReportRecyclerView.ViewHolder{
            TextView congratCardTitle;
            TextView congratCardContent;
            TextView congratCardSubcontent;

            public CongratViewHolder(View itemView) {
                super(itemView);

                congratCardTitle = itemView.findViewById(R.id.txt_congratcardTitle);
                congratCardContent = itemView.findViewById(R.id.txt_congratcardContent);
                congratCardSubcontent = itemView.findViewById(R.id.txt_congratcardSubcontent);
            }
        }

        class TryHarderViewHolder extends  ReportRecyclerView.ViewHolder{
            TextView tryHarderCardTitle;
            TextView tryHarderCardContent;
            TextView tryHarderCardSubcontent;

            public TryHarderViewHolder(View itemView) {
                super(itemView);

                tryHarderCardTitle = itemView.findViewById(R.id.txt_tryhardercardTitle);
                tryHarderCardContent = itemView.findViewById(R.id.txt_tryhardercardContent);
                tryHarderCardSubcontent = itemView.findViewById(R.id.txt_tryhardercardSubcontent);
            }
        }

        class AdviceViewHolder extends  ReportRecyclerView.ViewHolder{
            TextView adviceCardTitle;
            TextView adviceCardContent;
            TextView adviceCardSubcontent;

            public AdviceViewHolder(View itemView) {
                super(itemView);

                adviceCardTitle = itemView.findViewById(R.id.txt_advicecardTitle);
                adviceCardContent = itemView.findViewById(R.id.txt_advicecardContent);
                adviceCardSubcontent = itemView.findViewById(R.id.txt_advicecardSubcontent);
            }
        }

        class BarchartViewHolder extends ReportRecyclerView.ViewHolder{
            TextView barchartCardTitle;
            HorizontalBarChart barChartCardChart;

            public BarchartViewHolder(View itemView){
                super(itemView);

                barchartCardTitle = itemView.findViewById(R.id.txt_barchartcardTitle);
                barChartCardChart = itemView.findViewById(R.id.barchartcardChart);
            }
        }
    
    

        class DefaultViewHolder extends ReportRecyclerView.ViewHolder {
            TextView defaultCardTitle;
            TextView defaultCardContent;

            public DefaultViewHolder(View itemView) {
                super(itemView);

                defaultCardTitle = itemView.findViewById(R.id.txt_defaultcardTitle);
                defaultCardContent = itemView.findViewById(R.id.txt_defaultcardContent);

            }
        }

        @Override
        public int getItemViewType(int position) {

            return mDataset.get(position).getInt("ViewType", -1);

        }

        @Override
        @NonNull
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v;

            switch (viewType) {
                case 0:
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.report_headercard, parent, false);
                    return new HeaderViewHolder(v);
                case 1:
                    v = LayoutInflater.from(parent.getContext())
                           .inflate(R.layout.report_congratulationcard, parent, false);
                    return new CongratViewHolder(v);
                case 2:
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.report_tryhardercard, parent, false);
                    return new TryHarderViewHolder(v);
                case 3:
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.report_advicecard, parent, false);
                    return new AdviceViewHolder(v);
                case 4:
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.report_barchartcard, parent, false);
                    return new BarchartViewHolder(v);
                default:
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.report_defaultcard, parent, false);
                    return new DefaultViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            Bundle b = mDataset.get(position);
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderViewHolder headerViewHolder = (HeaderViewHolder)holder;
                    headerViewHolder.headerCardTitle
                            .setText(b.getString("Title", "Hello World!"));
                    headerViewHolder.headerCardContent
                            .setText(b.getString("Content", "What a great day!"));
                    headerViewHolder.headerCardTimeSelect.setAdapter(adapter);
                    if(b.getBoolean("UpdateSpinner", false)) {
                        if(!changeTimeSpinnerStrings.isEmpty())
                            changeTimeSpinnerStrings.clear();
                        changeTimeSpinnerStrings.addAll(b.getStringArrayList("spinnerkeys"));
                        adapter.notifyDataSetChanged();
                    }

                    //Prevent spinner listener triggering
                    headerViewHolder.headerCardTimeSelect.setSelected(false);
                    headerViewHolder.headerCardTimeSelect.setSelection(b.getInt("SelectedTime"), false);

                    //Prevent tablayout listener triggering
                    headerViewHolder.headerCardPeriodSelect.removeOnTabSelectedListener(changePeriodListener);
                    headerViewHolder.headerCardPeriodSelect.getTabAt(b.getInt("SelectedPeriod")).select();
                    headerViewHolder.headerCardPeriodSelect.addOnTabSelectedListener(changePeriodListener);

                    break;
                case 1:
                    CongratViewHolder congratViewHolder = (CongratViewHolder) holder;
                    congratViewHolder.congratCardTitle.setText(
                            b.getString("Title", "Error Displaying Content")
                    );
                    congratViewHolder.congratCardContent.setText(
                            b.getString("Content", "Error Displaying Content")
                    );
                    congratViewHolder.congratCardSubcontent.setText(
                            b.getString("Subcontent", "Error Displaying Content")
                    );
                    break;

                case 2:
                    TryHarderViewHolder tryHarderViewHolder = (TryHarderViewHolder) holder;
                    tryHarderViewHolder.tryHarderCardTitle.setText(
                            b.getString("Title", "Error Displaying Content")
                    );
                    tryHarderViewHolder.tryHarderCardContent.setText(
                            b.getString("Content", "Error Displaying Content")
                    );
                    tryHarderViewHolder.tryHarderCardSubcontent.setText(
                            b.getString("Subcontent", "Error Displaying Content")
                    );
                    break;
                    
                case 3:
                    AdviceViewHolder adviceViewHolder = (AdviceViewHolder) holder;
                    adviceViewHolder.adviceCardTitle.setText(
                            b.getString("Title", "Error Displaying Content")
                    );
                    adviceViewHolder.adviceCardContent.setText(
                            b.getString("Content", "Error Displaying Content")
                    );
                    adviceViewHolder.adviceCardSubcontent.setText(
                            b.getString("Subcontent", "Error Displaying Content")
                    );
                    break;

                case 4:
                    BarchartViewHolder barchartViewHolder = (BarchartViewHolder) holder;
                    barchartViewHolder.barchartCardTitle.setText(
                            b.getString("Title", "Error Displaying Content")
                    );
                    ArrayList<Column> columns = b.getParcelableArrayList("BarData");

                    //Reverse column order to force horizontal barchart to display from top to bottom.
                    Collections.reverse(columns);
                    BarData data = getBarData(columns,
                            b.getString("Label", ""));
                    barchartViewHolder.barChartCardChart.setData(data);

                    final ArrayList<String> xLabels = getXLabels(columns);
                    XAxis xAxis = barchartViewHolder.barChartCardChart.getXAxis();
                    xAxis.setDrawGridLines(false);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setValueFormatter(new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            int index = (int)value;
                            return xLabels.get(index);
                        }
                    });
                    if(b.containsKey("LimitLineVal")){
                        LimitLine limitLine = new LimitLine(
                                b.getFloat("LimitLineVal", 2000f)
                        );
                        limitLine.setLineWidth(2f);
                        limitLine.enableDashedLine(10f, 5f, 0f);

                        barchartViewHolder.barChartCardChart.getAxisLeft().addLimitLine(limitLine);

                    }

                    YAxis yAxis = barchartViewHolder.barChartCardChart.getAxisLeft();
                    yAxis.setAxisMinimum(0f);

                    if(b.containsKey("LimitLineVal")){
                        Float max = null;

                        for(Column column:columns){
                            if(max == null || column.getColVal() > max)
                                max = column.getColVal();
                        }

                        Float limitlineval = b.getFloat("LimitLineVal", 0f);

                        if(max > limitlineval)
                            limitlineval = max;

                        yAxis.setAxisMaximum(
                                limitlineval*1.05f
                        );
                    }

                    barchartViewHolder.barChartCardChart.getAxisRight().setDrawGridLines(false);
                    barchartViewHolder.barChartCardChart.getAxisRight().setDrawLabels(false);
                    barchartViewHolder.barChartCardChart.setScaleEnabled(false);

                    barchartViewHolder.barChartCardChart.getDescription().setText(
                            b.getString("Description", "")
                    );
                    barchartViewHolder.barChartCardChart.getLegend().setEnabled(false);
                    barchartViewHolder.barChartCardChart.animateXY(2000, 2000);
                    barchartViewHolder.barChartCardChart.invalidate();
                    break;

                default:
                    DefaultViewHolder defaultViewHolder = (DefaultViewHolder)holder;
                    defaultViewHolder.defaultCardTitle.setText(
                            b.getString("DefaultTitle", "Error Displaying Content")
                    );
                    defaultViewHolder.defaultCardContent.setText(
                            b.getString("DefaultContent", "ViewType not Found!")
                    );

            }
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        private BarData getBarData(ArrayList<Column> columns, String Label){

            ArrayList<BarEntry> valueSet = new ArrayList<>();

            for(int i = 0; i < columns.size(); i++){
                valueSet.add(new BarEntry(i, columns.get(i).getColVal()));
            }

            BarDataSet barDataSet = new BarDataSet(valueSet, Label);
            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            barDataSet.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    if (value == 0f)
                        return "";
                    return String.valueOf((int)value);
                }
            });


            return new BarData(barDataSet);
        }

        private ArrayList<String> getXLabels(ArrayList<Column> columns){

            ArrayList<String> XLabels = new ArrayList<>();

            for(Column column:columns){
                XLabels.add(column.getColName());
            }

            return  XLabels;
        }

}