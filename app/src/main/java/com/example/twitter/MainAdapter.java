package com.example.twitter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class MainAdapter  extends RecyclerView.Adapter<MainAdapter.MyViewHolder>{

    private final static String TAG = "MainAdapter";

    private List<MainData> dataList;
    private String userEmail;

    static public View.OnClickListener clickDelete;
    static public View.OnClickListener clickModification;

    static public View.OnClickListener clickUpdate;
    static public View.OnClickListener clickCancel;



    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout basicLayout, modificationLayout;
        private EditText modificationContent;
        private Button updateButton, cancelButton;

        public TextView content;
        public ImageButton delete, modification;

        public MyViewHolder(View v, int viewType) {
            super(v);

            content = (TextView) v.findViewById(R.id.content);



            // 내가 작성한 글일 경우
            if (viewType == 1) {

                basicLayout = (RelativeLayout) v.findViewById(R.id.basicLayout);
                modificationLayout = (RelativeLayout) v.findViewById(R.id.modificationLayout);

                modificationContent = (EditText) v.findViewById(R.id.modificationContent);

                updateButton = (Button) v.findViewById(R.id.updateButton);
                cancelButton = (Button) v.findViewById(R.id.cancelButton);

                delete = (ImageButton) v.findViewById(R.id.delete);
                modification = (ImageButton) v.findViewById(R.id.modification);

                delete.setOnClickListener(clickDelete);
                modification.setOnClickListener(clickModification);

                updateButton.setOnClickListener(clickUpdate);
                cancelButton.setOnClickListener(clickCancel);
            }

        }
    }

    public MainAdapter(List<MainData> getDataList, String getUserEmail,
                       View.OnClickListener deleteListener,
                       View.OnClickListener modificationListener,
                       View.OnClickListener updateListener,
                       View.OnClickListener cancelListener) {
        this.dataList = getDataList;
        this.userEmail = getUserEmail;
        clickDelete = deleteListener;
        clickModification = modificationListener;
        clickUpdate = updateListener;
        clickCancel = cancelListener;

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
        Log.d(TAG, String.valueOf(viewType) + " ViewHolder");
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
            holder.updateButton.setTag(position);
            holder.cancelButton.setTag(position);
            holder.modificationLayout.setVisibility(View.GONE);
            holder.basicLayout.setVisibility(View.VISIBLE);

            if (dataList.get(position).getModificationCheck()) {
                holder.basicLayout.setVisibility(View.GONE);
                holder.modificationLayout.setVisibility(View.VISIBLE);
                holder.modificationContent.setHint(dataList.get(position).getContent());
            }

        }

        holder.content.setText(dataList.get(position).getContent());
    }



    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

}
