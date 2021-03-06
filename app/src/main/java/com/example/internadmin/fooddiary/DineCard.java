package com.example.internadmin.fooddiary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.internadmin.fooddiary.Activities.CameraActivity;
import com.example.internadmin.fooddiary.Activities.MealActivity;
import com.example.internadmin.fooddiary.Models.FoodItem;
import com.example.internadmin.fooddiary.Models.Meal;
import com.example.internadmin.fooddiary.Models.TimePeriod;
import com.example.internadmin.fooddiary.Views.FoodItemAdapter;
import com.example.internadmin.fooddiary.Views.NonScrollListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DineCard extends CardView{
    /*----------------------------------------------------------------------
    This is the default class for the meal cards (morning, afternoon night).
    ----------------------------------------------------------------------*/
    // This is a custom cardview made for creating morning, afternoon, evening and night cards
    public DineCard(@NonNull Context context, Point size, String title, int imageresid, List<Long> mealdata, Calendar myCalendar, final TimePeriod timePeriod, int idstart) {
        super(context);
        // this is the main layout of the card
        LinearLayout dinelayout = new LinearLayout(getContext());
        dinelayout.setOrientation(LinearLayout.VERTICAL);
        // setting the layout parameters
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int marginx = (int)(size.x*0.027);
        int marginy = (int)(size.x*0.027);
        params.setMargins(marginx, marginy, marginx, marginy);
        this.setLayoutParams(params);
        this.setRadius(0);
        this.setCardBackgroundColor(getResources().getColor(R.color.cardcolor));
        this.setMaxCardElevation(15);
        this.setCardElevation(9);
        this.addView(dinelayout);
        // relative layout to contain the top view of each card (the view that shows morning/afternoon/night and their imagesd)
        RelativeLayout rl = new RelativeLayout(getContext());
        dinelayout.addView(rl);
        // text title on the top of card
        TextView dinehead = new TextView(getContext());
        dinehead.setText(title);
        dinehead.setId(idstart+1);
        // setting the layout parameters
        dinehead.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (size.x*0.085));
        RelativeLayout.LayoutParams dineparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dineparams.setMargins((int)(size.x*0.06), (int)(size.y*0.04), 0,(int)(size.x*0.05));
        ImageView dineimg = new ImageView(getContext());
        dineimg.setId(idstart+2);
        dineimg.setImageResource(imageresid);
        RelativeLayout.LayoutParams dineimgparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //dineimgparams.addRule(RelativeLayout.RIGHT_OF, dinehead.getId());
        dineimgparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        dineimgparams.setMargins(0, (int)(size.y*0.02), (int)(size.x*0.10),(int)(size.x*0.04));
        rl.addView(dinehead, dineparams);
        rl.addView(dineimg, dineimgparams);
        // Creating the listView that contains the list of fooditems consumed
        ArrayList<FoodItem> foodlist = new ArrayList<>();
        for(int i = 0; i < mealdata.size(); i++){
            Long id = mealdata.get(i);
            Meal meal = new Meal();
            meal.populateFromDatabase(id, getContext());
            foodlist.add(new FoodItem(meal.getDishID().getDisplayFoodName(), "yummy", id));
        }
        // using nonscrolllistview instead of normal listview because normal listview creates problems when used inside scrollview
        NonScrollListView dinelist = new NonScrollListView(getContext());
        final FoodItemAdapter adapter = new FoodItemAdapter(getContext(), R.layout.food_item, foodlist);
        dinelist.setAdapter(adapter);
        final Context finalContext = context;
        // on item click listener.. shows all data related to that particular food entry on click
        dinelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Long myitem = adapter.getItem(position).getId();
                Intent myintent = new Intent(getContext(), MealActivity.class);
                myintent.putExtra("Meal", myitem);
                finalContext.startActivity(myintent);
            }
        });
        dinelayout.addView(dinelist);

        //Adds a divider line between the food items and the add item
        if(!foodlist.isEmpty()){
            View divider = new View(getContext());
            divider.setBackgroundColor(getResources().getColor(R.color.cardcolor));
            divider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 3));
            dinelayout.addView(divider);
        }

        // layout to contain additem option
        LinearLayout addlayout = new LinearLayout(getContext());
        addlayout.setOrientation(LinearLayout.HORIZONTAL);
        addlayout.setGravity(Gravity.CENTER_VERTICAL);
        ImageView plus = new ImageView(getContext());
        plus.setId(idstart+3);
        plus.setImageResource(R.drawable.add);
        plus.setPadding((int)(size.x*0.05), (int)(size.x*0.02), 0, (int)(size.x*0.02));
        addlayout.addView(plus);
        // setting layout parameters for add item option
        TextView additem = new TextView(getContext());
        additem.setText("Add Item");
        additem.setId(idstart+4);
        additem.setPadding((int)(size.x*0.05), (int)(size.x*0.02), 0, (int)(size.x*0.02));
        addlayout.addView(additem);
        addlayout.setBackgroundColor(getResources().getColor(R.color.whitecolor));
        final Calendar tempcal = myCalendar;
        // additem adds entry for that particular date (the date chosen in the dateselect (default to current date if no date is chosen))
        addlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CameraActivity.class);
                intent.putExtra("mealtime", timePeriod);
                intent.putExtra("mealdate", tempcal.getTime().getTime());
                finalContext.startActivity(intent);
            }
        });
        dinelayout.addView(addlayout);
    }

    public DineCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
