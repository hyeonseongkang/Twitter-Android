package com.example.twitter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class MainAdapter  extends RecyclerView.Adapter<MainAdapter.MyViewHolder>{
    private List<MainData> dataList = new ArrayList<>();
    private String userEmail;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public MyViewHolder(View v) {
            super(v);
        }
    }

    public MainAdapter(List<MainData> getDataList, String getUserEmail) {
        this.dataList = getDataList;
        this.userEmail = getUserEmail;

    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position).getUser().equals(userEmail)) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public MainAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter, parent, false);
        if (viewType == 1) {
            // myView

        } else {
            // otherView
        }
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.textView.setText(dataList.get(position).getUser());
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

}
