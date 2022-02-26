package com.example.twitter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class MainAdapter  extends RecyclerView.Adapter<MainAdapter.MyViewHolder>{
    private final static String TAG = "MainAdapter";

    public Bitmap photoBitmap;

    static public FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    static public DatabaseReference myRef = firebaseDatabase.getReference("Main");

    static public List<MainData> dataList;
    static public List<Uri> photoUriList;
    public String userEmail;

    static public View.OnClickListener clickDelete;
    static public View.OnClickListener clickModification;

    static public View.OnClickListener clickCancel;

    static public View.OnClickListener clickPhotoChange;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout basicLayout, modificationLayout;
        private EditText modificationContent;
        private Button updateButton, cancelButton;

        public TextView content;
        public ImageButton delete, modification;

        private CircleImageView photo;

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

                photo = (CircleImageView) v.findViewById(R.id.photo);
                photo.setEnabled(true);
                photo.setClickable(true);

                photo.setOnClickListener(clickPhotoChange);

                delete.setOnClickListener(clickDelete);
                modification.setOnClickListener(clickModification);

                // content 수정하기
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Object object = view.getTag();
                        if (object != null) {
                            int position = (int) object;
                            if (modificationContent.getText().toString().length() != 0) {
                                String updateContent = modificationContent.getText().toString();
                                myRef.child(dataList.get(position).getKey()).child("modificationCheck").setValue(false);
                                myRef.child(dataList.get(position).getKey()).child("content").setValue(updateContent);
                            }
                        }
                    }
                });

                cancelButton.setOnClickListener(clickCancel);

            }

        }
    }

    public MainAdapter(List<MainData> getDataList, List<Uri>getPhotoUriList,String getUserEmail,
                       View.OnClickListener deleteListener,
                       View.OnClickListener modificationListener,
                       View.OnClickListener cancelListener,
                       View.OnClickListener photoChangeListener
                       ) {
        this.dataList = getDataList;
        this.photoUriList = getPhotoUriList;
        this.userEmail = getUserEmail;
        clickDelete = deleteListener;
        clickModification = modificationListener;
        clickCancel = cancelListener;
        clickPhotoChange = photoChangeListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position).getUser().equals(userEmail)) return 1;
        else return 2;

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
        if (getItemViewType(holder.getAdapterPosition()) == 1) {
            holder.delete.setTag(holder.getAdapterPosition());
            holder.modification.setTag(holder.getAdapterPosition());
            holder.updateButton.setTag(holder.getAdapterPosition());
            holder.cancelButton.setTag(holder.getAdapterPosition());
            holder.photo.setTag(holder.getAdapterPosition());
            holder.modificationLayout.setVisibility(View.GONE);
            holder.basicLayout.setVisibility(View.VISIBLE);
            holder.content.setText(dataList.get(holder.getAdapterPosition()).getContent());

            // 수정 버튼이 활성화 되었다면
            if (dataList.get(holder.getAdapterPosition()).getModificationCheck()) {
                holder.basicLayout.setVisibility(View.GONE);
                holder.modificationLayout.setVisibility(View.VISIBLE);
                holder.modificationContent.setText(dataList.get(holder.getAdapterPosition()).getContent());

                // tempPhotoListKey에 position 값이 있다면 -> temp에 저장된 사진이 있다면 해당 사진을 보여준다.
                if (MainActivity.tempPhotoListKey.contains(position)) {
                    int index = MainActivity.tempPhotoListKey.indexOf(position);
                    Log.d("ㅇㅕ기요", String.valueOf(index));
                    Log.d("여기요2", String.valueOf(MainActivity.tempPhotoList.get(index)));
                    Glide.with(holder.photo.getContext()).load(MainActivity.tempPhotoList.get(index)).into(holder.photo);
                } else {
                    // photoKey가 있다면 -> 사진까지 저장 했다면
                    if (dataList.get(holder.getAdapterPosition()).getPhotoKey() != null) {
                        Uri uri = Uri.parse(dataList.get(holder.getAdapterPosition()).getPhotoUri());
                        Glide.with(holder.photo.getContext()).load(uri).into(holder.photo); // Glide를 사용하여 이미지 로드

                    } else {
                        Glide.with(holder.photo.getContext()).load(R.drawable.gallery).into(holder.photo); // 기본 이미지 로드
                    }
                }

            } else {
                holder.modificationLayout.setVisibility(View.GONE);
                holder.basicLayout.setVisibility(View.VISIBLE);
            }
        } else {
            holder.content.setText(dataList.get(holder.getAdapterPosition()).getContent());
        }


    }


    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

}
