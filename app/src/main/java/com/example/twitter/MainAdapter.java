package com.example.twitter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class MainAdapter  extends RecyclerView.Adapter<MainAdapter.MyViewHolder>{
    private List<MainData> dataList;
    private String userEmail;

    static public View.OnClickListener clickDelete;
    static public View.OnClickListener clickModification;



    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView content;
        public ImageButton delete, modification;
        public MyViewHolder(View v, int viewType) {
            super(v);

            content = (TextView) v.findViewById(R.id.content);

            delete = (ImageButton) v.findViewById(R.id.delete);
            modification = (ImageButton) v.findViewById(R.id.modification);

            // 내가 작성한 글일 경우
            if (viewType == 1) {
                delete.setOnClickListener(clickDelete);
                modification.setOnClickListener(clickModification);
            }

        }
    }

    public MainAdapter(List<MainData> getDataList, String getUserEmail, View.OnClickListener deleteListener, View.OnClickListener modificationListener) {
        this.dataList = getDataList;
        this.userEmail = getUserEmail;
        clickDelete = deleteListener;
        clickModification = modificationListener;

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

        if (viewType == 1) {
            // myView
            v = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter, parent, false);
        } else {
            // otherView
            v = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter2, parent, false);
        }
        MyViewHolder viewHolder = new MyViewHolder(v, viewType);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // 내가 작성한 글일 경우
        if (getItemViewType(position) == 1) {
            holder.delete.setTag(position);
            holder.modification.setTag(position);
        }

        holder.content.setText(dataList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

}
