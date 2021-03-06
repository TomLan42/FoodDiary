package com.example.internadmin.fooddiary.Views;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.internadmin.fooddiary.Models.FoodItem;
import com.example.internadmin.fooddiary.R;

import java.util.ArrayList;

public class FoodItemAdapter extends ArrayAdapter<FoodItem> {
    public FoodItemAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<FoodItem> objects){
        super(context, resource, objects);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.food_item, null);
        }
        FoodItem foodItem = getItem(position);
        if(foodItem != null){
            TextView title = convertView.findViewById(R.id.foodtitle);
            title.setText(foodItem.getTitle());
        }
        return convertView;
    }
}
