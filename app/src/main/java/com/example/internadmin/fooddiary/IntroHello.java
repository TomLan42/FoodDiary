package com.example.internadmin.fooddiary;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class IntroHello extends Fragment {
    private EditText editname;
    private EditText editage;
    public IntroHello() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro_hello, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editname = getView().findViewById(R.id.name_field);
        editage = getView().findViewById(R.id.age_field);
    }
    public String getname(){
        return editname.getText().toString();
    }
    public int getage(){
        return Integer.parseInt(editage.getText().toString());
    }
}
