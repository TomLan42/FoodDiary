package com.example.internadmin.fooddiary.Views;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.internadmin.fooddiary.Config;
import com.example.internadmin.fooddiary.Models.NutritionDefaults;
import com.example.internadmin.fooddiary.R;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;
import com.github.paolorotolo.appintro.ISlideSelectionListener;

import java.util.List;

public class GetNutritionPrefFragment extends Fragment implements ISlideSelectionListener,
        ISlideBackgroundColorHolder {

    private ImageView img_nutritionfragment;
    private Spinner spinner_nutritiontype;
    private EditText edittxt_nutritionval;
    private SharedPreferences prefs;

    protected static final String ARG_DRAWABLE = "drawable";
    protected static final String ARG_BGCOLOR = "bg_color";


    private int drawable, bgColor;

    private LinearLayout mainLayout;

    public GetNutritionPrefFragment(){ }

    public static GetNutritionPrefFragment newInstance(int drawable, int bgColor){
        GetNutritionPrefFragment fragment = new GetNutritionPrefFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DRAWABLE, drawable);
        args.putInt(ARG_BGCOLOR, bgColor);

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if(getArguments() != null && getArguments().size() !=0){
            drawable = getArguments().getInt(ARG_DRAWABLE);
            bgColor = getArguments().getInt(ARG_BGCOLOR);

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            drawable = savedInstanceState.getInt(ARG_DRAWABLE);
            bgColor = savedInstanceState.getInt(ARG_BGCOLOR);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_getnutritionpref, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        final SharedPreferences.Editor edit = prefs.edit();

        mainLayout = view.findViewById(R.id.fragment_nutritionprefll);
        img_nutritionfragment = view.findViewById(R.id.img_nutritionfragment);
        edittxt_nutritionval = view.findViewById(R.id.edittxt_nutritionval);

        spinner_nutritiontype = view.findViewById(R.id.spinner_nutritiontype);
        List<NutritionDefaults> mynutritiontypes = Config.NUTRITION_DEFAULTS_ARRAY_LIST;
        ArrayAdapter<NutritionDefaults> adapter = new ArrayAdapter<>(getContext(),
                R.layout.my_simple_item, mynutritiontypes);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_nutritiontype.setAdapter(adapter);
        spinner_nutritiontype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                NutritionDefaults mynutrition = (NutritionDefaults) spinner_nutritiontype.getSelectedItem();
                edittxt_nutritionval.setText(String.valueOf(mynutrition.getDefaultvalue()));
                edit.putString(getString(R.string.nutrition_to_track),mynutrition.getInternalNutrition());
                edit.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        edittxt_nutritionval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                float a = -1f;
                try{
                    a = Float.valueOf(edittxt_nutritionval.getText().toString());
                }catch (NumberFormatException e){
                    Log.i("NutritionPref" , "NumberFormatException" + e.getMessage());
                }
                edit.putFloat(getString(R.string.tracking_nutrition_limit), a);
                edit.apply();
            }
        });


        img_nutritionfragment.setImageResource(drawable);
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