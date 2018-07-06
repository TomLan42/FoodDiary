package com.example.internadmin.fooddiary.Views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.internadmin.fooddiary.R;

public class Report extends Fragment {

    private ReportRecyclerView mRecyclerView;
    private ReportRecyclerView.Adapter mAdapter;
    private ReportRecyclerView.LayoutManager mLayoutManager;

    public Report() {
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
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = view.findViewById(R.id.reportrecyclerview);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Bundle b = new Bundle();
        b.putString("Title", "WASSSUPPPP");
        b.putString("Content", "MY MANNNNN");

        Bundle[] myDataset = { b, new Bundle(), new Bundle() };

        // specify an adapter (see also next example)
        mAdapter = new ReportRecyclerViewAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);

    }

}
