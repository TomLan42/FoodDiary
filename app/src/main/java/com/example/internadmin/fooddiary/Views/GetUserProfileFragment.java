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
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.internadmin.fooddiary.R;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;
import com.github.paolorotolo.appintro.ISlideSelectionListener;

public class GetUserProfileFragment extends Fragment implements ISlideSelectionListener,
        ISlideBackgroundColorHolder{

    private SharedPreferences prefs;

    protected static final String ARG_BGCOLOR = "bg_color";

    private int bgColor;

    private EditText editTextAge;
    private EditText editTextHeight;
    private EditText editTextWeight;


    private LinearLayout mainLayout;

    RadioButton male;
    RadioButton female;

    public GetUserProfileFragment(){ }

    public static GetUserProfileFragment newInstance(int bgColor){
        GetUserProfileFragment fragment = new GetUserProfileFragment();
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
        return inflater.inflate(R.layout.fragment_get_user_profile,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        final SharedPreferences.Editor edit = prefs.edit();


        mainLayout = view.findViewById(R.id.fragment_userprofilell);
        RadioGroup radioSexGroup = view.findViewById(R.id.radioGender);
        editTextAge = view.findViewById(R.id.edittxt_age);
        editTextHeight = view.findViewById(R.id.edittxt_height);
        editTextWeight = view.findViewById(R.id.edittxt_weight);
        male = view.findViewById(R.id.radioMale);
        female = view.findViewById(R.id.radioFemale);

        if(!prefs.contains("Sex"))
            edit.putInt("Sex", 0);

        radioSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                
                int gender;
                
                if(checkedId == R.id.radioMale)
                    gender = 0;
                else if (checkedId == R.id.radioFemale)
                    gender = 1;
                else
                    gender = 2;
                
                edit.putInt("Sex", gender);
                edit.apply();
                
            }
        });
        
        editTextAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String mystr = editTextAge.getText().toString();
                if(!mystr.isEmpty()){
                    int myage = Integer.valueOf(mystr);
                    edit.putInt("Age", myage);
                    edit.apply();
                }else{
                    edit.remove("Age").apply();
                }
            }
        });

        editTextHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String mystr = editTextHeight.getText().toString();
                if(!mystr.isEmpty()) {
                    int height = Integer.valueOf(mystr);
                    edit.putInt("Height", height);
                    edit.apply();
                }else{
                    edit.remove("Height").apply();
                }
            }
        });

        editTextWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String mystr = editTextWeight.getText().toString();
                if(!mystr.isEmpty()) {
                    int weight = Integer.valueOf(mystr);
                    edit.putInt("Weight", weight);
                    edit.apply();
                }else{
                    edit.remove("Weight").apply();
                }
            }
        });
        
        

        mainLayout.setBackgroundColor(bgColor);

    }


    @Override
    public void onSlideSelected() {

        switch (prefs.getInt("Sex",0)){
            case 0:
                male.setChecked(true);
                break;
            case 1:
                female.setChecked(true);
        }

        if(prefs.contains("Age")){
            editTextAge.setText(String.valueOf(prefs.getInt("Age", 30)));
        }

        if(prefs.contains("Height")){
            editTextHeight.setText(String.valueOf(prefs.getInt("Height", 160)));
        }

        if(prefs.contains("Weight")){
            editTextWeight.setText(String.valueOf(prefs.getInt("Weight", 50)));
        }

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
