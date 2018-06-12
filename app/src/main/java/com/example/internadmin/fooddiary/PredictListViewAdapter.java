package com.example.internadmin.fooddiary;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PredictListViewAdapter extends ArrayAdapter<Prediction> {


    public PredictListViewAdapter(Activity context, ArrayList<Prediction> myPredictions) {
        super(context, R.layout.row_layout_prediction, myPredictions);

    }

    private static class ViewHolder{
        TextView predictedFoodName;
        ImageButton helpbtn;
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
                //Create the intent to start another activity
                Toast.makeText(getContext(), getItem(position).getFoodName(), Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(view.getContext(), AnotherActivity.class);
                //startActivity(intent);
            }
        });

        viewHolder.predictedFoodName.setText(mypredict.getFoodName());
        //int displposition = position + 1;
        return convertView;

    };

}
