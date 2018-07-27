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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.internadmin.fooddiary.R;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;
import com.github.paolorotolo.appintro.ISlideSelectionListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Fragment which is used by IntroActivity.
 *
 * Requests for more information regarding the preferences that the user has made,
 * based on GetNutritionTrackFragment.
 */

public class MoreInfoFragment extends Fragment implements ISlideSelectionListener,
        ISlideBackgroundColorHolder{

    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    protected static final String ARG_BGCOLOR = "bg_color";

    private int bgColor;

    private boolean wl = false;
    private boolean db = false;
    //private boolean hbp = false;


    private LinearLayout mainLayout;
    private LinearLayout wtlossLayout;
    //private LinearLayout HBPLayout;
    private LinearLayout DiabetesLayout;

    private TextView nouserprofilewarning;

    private EditText moreinfoweight;
    private EditText moreinfowtweeks;

    //private RadioGroup moreinfoHBP;
    //private EditText editText_CustomDASH;

    private RadioGroup moreinfoDiabetes;
    private EditText editText_CustomCarbs;

    public MoreInfoFragment(){ }

    public static MoreInfoFragment newInstance(int bgColor){
        MoreInfoFragment fragment = new MoreInfoFragment();
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
        return inflater.inflate(R.layout.fragment_more_info,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        edit = prefs.edit();

        mainLayout = view.findViewById(R.id.fragment_moreinfoll);
        wtlossLayout = view.findViewById(R.id.moreinfo_wtloss);
        //HBPLayout = view.findViewById(R.id.moreinfo_hbp);
        DiabetesLayout = view.findViewById(R.id.moreinfo_sugar);

        nouserprofilewarning = view.findViewById(R.id.nouserprofilewarning);

        moreinfoweight = view.findViewById(R.id.edittxt_moreinfoweight);
        moreinfowtweeks = view.findViewById(R.id.edittxt_moreinfoweightwks);

        //moreinfoHBP = view.findViewById(R.id.radioHBPRegime);
        //editText_CustomDASH = view.findViewById(R.id.edittxt_CustomSalt);

        moreinfoDiabetes = view.findViewById(R.id.radioDiabetesRegime);
        editText_CustomCarbs = view.findViewById(R.id.edittxt_CustomCarbs);

        mainLayout.setBackgroundColor(bgColor);

    }


    @Override
    public void onSlideSelected() {
        wl = prefs.getBoolean("weightloss", false);
        db = prefs.getBoolean("diabetes", false);
        //hbp = prefs.getBoolean("hbpressure", false);

        if(wl){
            wtlossLayout.setVisibility(View.VISIBLE);
            setWtLossListeners();

            if(prefs.contains("Sex") && prefs.contains("Height") && prefs.contains("Weight") && prefs.contains("Age"))
                nouserprofilewarning.setVisibility(View.GONE);
            else
                nouserprofilewarning.setVisibility(View.VISIBLE);

        }
        else {
            wtlossLayout.setVisibility(View.GONE);
            //edit.remove(getString(R.string.DesiredWeight));
            //edit.remove(getString(R.string.DesiredWeightWks));
            //edit.apply();
        }

        if(db){
            DiabetesLayout.setVisibility(View.VISIBLE);
            setDiabetesListener();
        } else{
            DiabetesLayout.setVisibility(View.GONE);
            //edit.remove(getString(R.string.diabetesregime));
            //edit.remove(getString(R.string.customdiabetesregime));
            //edit.apply();
        }

        /*
        if(hbp){
            HBPLayout.setVisibility(View.VISIBLE);
            setHBPListeners();
        } else{
            HBPLayout.setVisibility(View.GONE);
            //edit.remove("HBPRegime");
            //edit.remove("CustomHBPRegime");
            //edit.apply();
        }
        */


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

    private void setWtLossListeners(){

        moreinfoweight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String mydesiredwt = moreinfoweight.getText().toString();

                int desiredwt;

                if(TextUtils.isEmpty(mydesiredwt))
                    desiredwt = 0;
                else
                    desiredwt = Integer.valueOf(mydesiredwt);

                if(desiredwt > 30){

                    edit.putInt(getString(R.string.DesiredWeight), desiredwt);
                    edit.apply();

                    if(prefs.contains("Weight")){
                        int currentwt = prefs.getInt("Weight", 70);
                        int wtdifference = currentwt-desiredwt;

                        int noofweeks = Math.round(Math.abs(2*wtdifference));

                        moreinfowtweeks.setText(String.valueOf(noofweeks));
                    }
                }else{
                    if(prefs.contains(getString(R.string.DesiredWeight)))
                        edit.remove(getString(R.string.DesiredWeight)).apply();
                }
            }
        });

        moreinfowtweeks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                String mydesiredwtwks = moreinfowtweeks.getText().toString();

                int desiredwtwks;

                if(!TextUtils.isEmpty(mydesiredwtwks)){
                    desiredwtwks = Integer.valueOf(mydesiredwtwks);
                    edit.putInt(getString(R.string.DesiredWeightWks), desiredwtwks);
                    edit.apply();
                }else{
                    if(prefs.contains(getString(R.string.DesiredWeightWks)))
                        edit.remove(getString(R.string.DesiredWeightWks)).apply();
                }

            }
        });


    }

    /*
    private void setHBPListeners(){

        moreinfoHBP.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.radioStdSalt:
                        editText_CustomDASH.setEnabled(false);
                        edit.putInt("HBPRegime", 0);
                        break;
                    case R.id.radioRecSalt:
                        editText_CustomDASH.setEnabled(false);
                        edit.putInt("HBPRegime", 1);
                        break;
                    case R.id.radioCustomSalt:
                        editText_CustomDASH.setEnabled(true);
                        edit.putInt("HBPRegime", 2);
                        editText_CustomDASH.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                                String customSodium = editText_CustomDASH.getText().toString();

                                int mycustomSodium;

                                if (!TextUtils.isEmpty(customSodium)) {
                                    mycustomSodium = Integer.valueOf(customSodium);
                                    edit.putInt("CustomHBPRegime", mycustomSodium);
                                    edit.apply();
                                }

                            }
                        });
                        break;
                    default:
                        break;
                }

                edit.apply();


            }
        });
    }*/

    private void setDiabetesListener(){

        moreinfoDiabetes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.radioRecCarbs:
                        editText_CustomCarbs.setEnabled(false);
                        edit.putInt(getString(R.string.diabetesregime), 0);
                        break;
                    case R.id.radioCustomCarbs:
                        editText_CustomCarbs.setEnabled(true);
                        edit.putInt(getString(R.string.diabetesregime), 1);
                        editText_CustomCarbs.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                                String customCarbs = editText_CustomCarbs.getText().toString();

                                int mycustomCarbs;

                                if (!TextUtils.isEmpty(customCarbs)) {
                                    mycustomCarbs = Integer.valueOf(customCarbs);
                                    edit.putInt(getString(R.string.customdiabetesregime), mycustomCarbs);
                                    edit.apply();
                                }else{
                                    edit.remove(getString(R.string.customdiabetesregime)).apply();
                                }

                            }
                        });
                        break;
                    default:
                        break;
                }

                edit.apply();

            }
        });
    }


}