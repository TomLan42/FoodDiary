package com.example.internadmin.fooddiary;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class PieSliderDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    private Button set;
    private ServingSliceListener listener;
    private PizzaView myPizzaView;
    private float ServingSlice;

    public PieSliderDialog(Activity a, float ServingSlice, ServingSliceListener listener) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.listener = listener;
        this.ServingSlice = ServingSlice;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pieslider_dialog);
        myPizzaView = (PizzaView) findViewById(R.id.ViewPizza);
        myPizzaView.setServingSlice(ServingSlice);
        set = (Button) findViewById(R.id.btn_setslice);
        set.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_setslice)
            listener.onDialogComplete(myPizzaView.getServingSlice());
        dismiss();
    }
}