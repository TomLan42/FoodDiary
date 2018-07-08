package com.example.internadmin.fooddiary.Views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.internadmin.fooddiary.R;

import java.util.ArrayList;

public class ReportRecyclerViewAdapter extends RecyclerView.Adapter {

        private ArrayList<Bundle> mDataset;
        private TabLayout.OnTabSelectedListener changePeriodListener;

        public ReportRecyclerViewAdapter(ArrayList<Bundle> myDataset,
                                         TabLayout.OnTabSelectedListener changePeriodListener) {
            this.mDataset = myDataset;
            this.changePeriodListener = changePeriodListener;
        }

        class HeaderViewHolder extends ReportRecyclerView.ViewHolder {
            TextView headerCardTitle;
            TextView headerCardContent;
            TabLayout headerCardPeriodSelect;

            HeaderViewHolder(View itemView){
                super(itemView);

                headerCardTitle = itemView.findViewById(R.id.txt_headercardTitle);
                headerCardContent = itemView.findViewById(R.id.txt_headercardContent);
                headerCardPeriodSelect = itemView.findViewById(R.id.periodtabs);

                headerCardPeriodSelect.addTab(headerCardPeriodSelect.newTab().setText("Daily"));
                headerCardPeriodSelect.addTab(headerCardPeriodSelect.newTab().setText("Weekly"));
                headerCardPeriodSelect.addTab(headerCardPeriodSelect.newTab().setText("Monthly"));

            }
        }

        class SummaryViewHolder extends ReportRecyclerView.ViewHolder {

            public SummaryViewHolder(View itemView) {
                super(itemView);

            }
        }

        @Override
        public int getItemViewType(int position) {
            // Just as an example, return 0 or 2 depending on position
            // Note that unlike in ListView adapters, types don't have to be contiguous
            return position;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;

            switch (viewType) {
               /* case 0:
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.report_headercard, parent, false);
                    return new ViewHolder0(v);
                case 2:
                    //v = LayoutInflater.from(parent.getContext())
                    //       .inflate(R.layout.some_other_layout, parent, false);
                    return new ViewHolder2(v);*/
                default:
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.report_headercard, parent, false);
                    return new HeaderViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            Bundle b = mDataset.get(position);
            switch (holder.getItemViewType()) {
                /*case 0:
                    ViewHolder0 viewHolder0 = (ViewHolder0)holder;

                    break;

                case 2:
                    ViewHolder2 viewHolder2 = (ViewHolder2)holder;

                    break;*/
                case 0:
                    HeaderViewHolder headerViewHolder = (HeaderViewHolder)holder;
                    headerViewHolder.headerCardTitle
                            .setText(b.getString("Title", "Hello World!"));
                    headerViewHolder.headerCardContent
                            .setText(b.getString("Content", "What a great day!"));
                    //headerViewHolder.headerCardPeriodSelect.getTabAt(0).select();
                    headerViewHolder.headerCardPeriodSelect.addOnTabSelectedListener(changePeriodListener);
                    break;

                default:
                    HeaderViewHolder defaultViewHolder = (HeaderViewHolder)holder;
                    defaultViewHolder.headerCardTitle
                            .setText(b.getString("Title", "Hello World!"));
                    defaultViewHolder.headerCardContent
                            .setText(b.getString("Content", "What a great day!"));
                    defaultViewHolder.headerCardPeriodSelect.getTabAt(0).select();
            }
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

}