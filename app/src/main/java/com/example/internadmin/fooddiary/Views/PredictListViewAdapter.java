package com.example.internadmin.fooddiary.Views;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.internadmin.fooddiary.Interfaces.DishIDPopulatedListener;
import com.example.internadmin.fooddiary.Models.DishID;
import com.example.internadmin.fooddiary.Models.Prediction;
import com.example.internadmin.fooddiary.R;

import java.util.ArrayList;

public class PredictListViewAdapter extends ArrayAdapter<Prediction> {

    //int selectedPosition = 0;
    private Boolean firstTime = true;


    public PredictListViewAdapter(Activity context, ArrayList<Prediction> myPredictions) {
        super(context, R.layout.row_layout_prediction, myPredictions);

    }

    private static class ViewHolder{
        LinearLayout row_layout_prediction;
        TextView predictedFoodName;
        ImageButton helpbtn;
        CheckBox predictedSelect;
        TextView predictedBestMatch;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView == null){

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_layout_prediction, parent, false);
            viewHolder.predictedFoodName = (TextView) convertView.findViewById(R.id.predictedFoodName);
            viewHolder.helpbtn = (ImageButton) convertView.findViewById(R.id.btn_predictionHelp);
            viewHolder.predictedSelect = (CheckBox) convertView.findViewById(R.id.checkBox_predictionSelect);
            viewHolder.predictedBestMatch = (TextView) convertView.findViewById(R.id.bestMatchPredict);
            viewHolder.row_layout_prediction = (LinearLayout) convertView.findViewById(R.id.row_layout_prediction);

            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Prediction mypredict = getItem(position);
        viewHolder.helpbtn.setTag(position);

        viewHolder.helpbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                final DishID mydishid = new DishID(getItem(position).getFoodName(), getItem(position).getVer(), getContext());
                mydishid.setDishIDPopulatedListener( new DishIDPopulatedListener() {
                    @Override
                    public void onPopulated(boolean dataAdded) {

                        if(dataAdded){
                            AlertDialog.Builder alertadd = new AlertDialog.Builder(getContext());
                            LayoutInflater factory = LayoutInflater.from(getContext());
                            final View view = factory.inflate(R.layout.predictionactivityhelp_dialog, null);
                            ImageView dishimg = view.findViewById(R.id.dialog_imageview);
                            dishimg.setImageBitmap(mydishid.getFoodImg());
                            alertadd.setView(view);
                            alertadd.setTitle(mydishid.getDisplayFoodName());
                            alertadd.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dlg, int sumthin) {
                                    dlg.dismiss();
                                }
                            });

                            alertadd.show();
                        }else{
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Error")
                                    .setMessage("Could not receive Dish data.")
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }

                    }
                });
                mydishid.execute();

                //Toast.makeText(getContext(), getItem(position).getDisplayFoodName(), Toast.LENGTH_LONG).show();

            }
        });

        viewHolder.predictedFoodName.setText(mypredict.getDisplayFoodName());

        if(position == 0){
            viewHolder.row_layout_prediction.setBackgroundColor(Color.parseColor("#a4c639"));
            viewHolder.predictedFoodName.setTextColor(Color.parseColor("#ffffff"));
            viewHolder.predictedBestMatch.setText("Best Match");
            viewHolder.predictedBestMatch.setTextColor(Color.parseColor("#ffffff"));
            if(firstTime){
                firstTime = false;
                viewHolder.predictedSelect.setChecked(true);
            }
        } else if (position == 1){
            viewHolder.predictedFoodName.setTextColor(Color.parseColor("#a4c639"));
            viewHolder.predictedBestMatch.setTextColor(Color.parseColor("#a4c639"));
            viewHolder.predictedBestMatch.setText("2nd Best Match");
        }

        return convertView;

    };

}
