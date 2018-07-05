package com.example.internadmin.fooddiary.Views;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.internadmin.fooddiary.R;

public class ReportRecyclerViewAdapter extends RecyclerView.Adapter {

        private String[] mDataset;

        public ReportRecyclerViewAdapter(String[] myDataset) {
            mDataset = myDataset;
        }

        class ViewHolder0 extends ReportRecyclerView.ViewHolder {

            public ViewHolder0(View itemView){
                super(itemView);

            }
        }

        class ViewHolder2 extends ReportRecyclerView.ViewHolder {

            public ViewHolder2(View itemView) {
                super(itemView);

            }
        }

        @Override
        public int getItemViewType(int position) {
            // Just as an example, return 0 or 2 depending on position
            // Note that unlike in ListView adapters, types don't have to be contiguous
            return position % 2 * 2;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = null;

            switch (viewType) {
                case 0:
                    //v = LayoutInflater.from(parent.getContext())
                    //        .inflate(R.layout.some_layout, parent, false);
                    return new ViewHolder0(v);
                case 2:
                    //v = LayoutInflater.from(parent.getContext())
                    //       .inflate(R.layout.some_other_layout, parent, false);
                    return new ViewHolder2(v);
                default:
                    //v = LayoutInflater.from(parent.getContext())
                    //        .inflate(R.layout.some_more_layout, parent, false);
                    return new ViewHolder2(v);
            }
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    ViewHolder0 viewHolder0 = (ViewHolder0)holder;

                    break;

                case 2:
                    ViewHolder2 viewHolder2 = (ViewHolder2)holder;

                    break;
            }
        }

        @Override
        public int getItemCount() {
            return mDataset.length;
        }

}