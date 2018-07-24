package com.example.internadmin.fooddiary.Views;


import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.internadmin.fooddiary.R;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;
import com.github.paolorotolo.appintro.ISlideSelectionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Fragment in IntroActivity which asks the user his usual serving size
 *
 */

public class GetDefaultServingFragment extends Fragment implements ISlideSelectionListener,
        ISlideBackgroundColorHolder{

    private SharedPreferences prefs;

    protected static final String ARG_BGCOLOR = "bg_color";

    private int bgColor;

    private LinearLayout mainLayout;
    private SeekBar seekbar_defaultservingsize;
    private EditText edittxt_defaultservingsize;

    float defaultservingsize;


    public GetDefaultServingFragment(){ }

    public static GetDefaultServingFragment newInstance(int bgColor){
        GetDefaultServingFragment fragment = new GetDefaultServingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_BGCOLOR, bgColor);

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if(getArguments() != null && getArguments().size() !=0){

            bgColor = getArguments().getInt(ARG_BGCOLOR);

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            bgColor = savedInstanceState.getInt(ARG_BGCOLOR);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_default_serving,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());


        mainLayout = view.findViewById(R.id.fragment_defaultservingll);
        seekbar_defaultservingsize = view.findViewById(R.id.seekbar_defaultservingsize);
        edittxt_defaultservingsize = view.findViewById(R.id.edittxt_defaultservingsize);


        final SharedPreferences.Editor edit = prefs.edit();

        if(!prefs.contains(getString(R.string.defaultservingsize)))
            edit.putFloat(getString(R.string.defaultservingsize), 1.0f).apply();
        
        defaultservingsize = prefs.getFloat(getString(R.string.defaultservingsize), 1.0f);

        seekbar_defaultservingsize.setMax(30);
        if(defaultservingsize > 3)
            seekbar_defaultservingsize.setProgress(30);
        else
            seekbar_defaultservingsize.setProgress(Math.round(defaultservingsize*10));
        seekbar_defaultservingsize.incrementProgressBy(1);

        edittxt_defaultservingsize.setText(String.format("%.2f", defaultservingsize));


        seekbar_defaultservingsize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                if(fromUser){
                    defaultservingsize = progress/10f;
                    edittxt_defaultservingsize.setText(String.format("%.2f", defaultservingsize));
                    edit.putFloat(getString(R.string.defaultservingsize), defaultservingsize);
                    edit.apply();
                }

            }
        });


        edittxt_defaultservingsize.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String mystr = edittxt_defaultservingsize.getText().toString();

                if(!TextUtils.isEmpty(mystr)) {
                    defaultservingsize = Float.valueOf(mystr);

                    edit.putFloat(getString(R.string.defaultservingsize), defaultservingsize);
                    edit.apply();

                    if(defaultservingsize > 3)
                        seekbar_defaultservingsize.setProgress(30);
                    else
                        seekbar_defaultservingsize.setProgress(Math.round(defaultservingsize*10));
                }
            }
        });


        mainLayout.setBackgroundColor(bgColor);


    }


    @Override
    public void onSlideSelected() {



    }

    @Override
    public void onSlideDeselected() {


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