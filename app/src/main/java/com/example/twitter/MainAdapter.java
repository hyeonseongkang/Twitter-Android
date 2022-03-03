package com.example.twitter;

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
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class MainAdapter  extends RecyclerView.Adapter<MainAdapter.MyViewHolder>{
    private final static String TAG = "MainAdapter";

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

        private CircleImageView photo, image;

        FirebaseController firebaseController = new FirebaseController();

        public MyViewHolder(View v, int viewType) {
            super(v);

            content = (TextView) v.findViewById(R.id.content);
            image = (CircleImageView) v.findViewById(R.id.image);

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
                    @RequiresApi(api = Build.VERSION_CODES.P)
                    @Override
                    public void onClick(View view) {
                        Object object = view.getTag();
                        MainActivity.progressBar.setVisibility(View.VISIBLE);
                        if (object != null) {
                            int position = (int) object;
                            if (modificationContent.getText().toString().length() != 0) {
                                String key = dataList.get(position).getKey();
                                String updateContent = modificationContent.getText().toString();


                                int index = MainActivity.tempPhotoListKey.indexOf(position);

                                if (index != -1) {
                                    Bitmap photoBitmap = null;
                                    try {
                                        photoBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(view.getContext().getContentResolver(), MainActivity.tempPhotoList.get(index)));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    firebaseController.writeData(key, updateContent, photoBitmap, true, index);

                                    /*
                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                    StorageReference storageRef = storage.getReference();

                                    String photoKey = dataList.get(position).getKey() + ".jpg";
                                    StorageReference storageReference = storageRef.child(photoKey);

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] data = baos.toByteArray();

                                    UploadTask uploadTask = storageReference.putBytes(data);

                                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                            Log.d(TAG, "Upload is " + progress + "% done");
                                            if (progress == 100.0) {
                                                storageRef.child(photoKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String photoUri = String.valueOf(uri);

                                                        myRef.child(key).setValue(new MainData(key, dataList.get(position).getUser(), updateContent, photoKey, false, photoUri));


                                                        myRef.child(key).child("content").setValue(updateContent);
                                                        myRef.child(key).child("photoUri").setValue(photoUri);
                                                        myRef.child(key).child("photoKey").setValue(photoKey);
                                                        myRef.child(key).child("modificationCheck").setValue(false);

                                                        위와 같이 코드를 작성한다면 MainAcitivty->getData()는 4번 호출 됨
                                                        addValueEventListener()-> Child가 변경될 때 마다 호출되고 위의 코드는 4번 child를 변경하므로 child를 변경할 때마다 addValueEventListener()가 호출됨


                                                        Log.d(TAG, "Save Complete");
                                                        MainActivity.tempPhotoList.remove(index);
                                                        MainActivity.tempPhotoListKey.remove(index);

                                                    }
                                                });

                                            }

                                        }
                                    }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                            Log.d(TAG, "Upload is paused");
                                            MainActivity.progressBar.setVisibility(View.GONE);
                                        }
                                    });*/

                                } else {
                                    myRef.child(key).child("content").setValue(updateContent);
                                }
                                MainActivity.progressBar.setVisibility(View.GONE);
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
            // myTweet
            v = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.my_tweet, parent, false);
        } else {
            // Tweet
            v = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet, parent, false);
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

            holder.image.setVisibility(View.VISIBLE);

//            Glide.with(holder.image.getContext())
//                    .load(dataList.get(holder.getAdapterPosition()).getPhotoUri() != null ? dataList.get(holder.getAdapterPosition()).getPhotoUri() : 0)
//                    .into(holder.image);


            // 수정 버튼이 활성화 되었다면
            if (dataList.get(holder.getAdapterPosition()).getModificationCheck()) {
                holder.basicLayout.setVisibility(View.GONE);
                holder.modificationLayout.setVisibility(View.VISIBLE);
                holder.modificationContent.setText(dataList.get(holder.getAdapterPosition()).getContent());

                // tempPhotoListKey에 position 값이 있다면 -> temp에 저장된 사진이 있다면 해당 사진을 보여준다.
                if (MainActivity.tempPhotoListKey.contains(position)) {
                    int index = MainActivity.tempPhotoListKey.indexOf(position);
                    Glide.with(holder.photo.getContext()).load(MainActivity.tempPhotoList.get(index)).into(holder.photo);
                } else {
                    // photoKey, photoUri가 있다면 -> 사진까지 저장 했다면
                    if (dataList.get(holder.getAdapterPosition()).getPhotoKey() != null && dataList.get(holder.getAdapterPosition()).getPhotoUri() != null) {
                        Uri uri = Uri.parse(dataList.get(holder.getAdapterPosition()).getPhotoUri());
                        Glide.with(holder.photo.getContext()).load(uri).into(holder.photo); // Glide를 사용하여 이미지 로드
                    } else {
                        Glide.with(holder.photo.getContext()).load(R.drawable.gallery).into(holder.photo); // 기본 이미지 로드
                    }
                }
            } else {
                holder.modificationLayout.setVisibility(View.GONE);
                holder.basicLayout.setVisibility(View.VISIBLE);
                if (dataList.get(holder.getAdapterPosition()).getPhotoKey() != null && dataList.get(holder.getAdapterPosition()).getPhotoUri() != null) {
                    Uri uri = Uri.parse(dataList.get(holder.getAdapterPosition()).getPhotoUri());
                    Glide.with(holder.image.getContext()).load(uri).into(holder.image); // Glide를 사용하여 이미지 로드
                } else {
                    holder.image.setVisibility(View.GONE);
                }
            }
        } else {
            holder.content.setText(dataList.get(holder.getAdapterPosition()).getContent());
            holder.image.setVisibility(View.VISIBLE);
            if (dataList.get(holder.getAdapterPosition()).getPhotoKey() != null && dataList.get(holder.getAdapterPosition()).getPhotoUri() != null) {
                Uri uri = Uri.parse(dataList.get(holder.getAdapterPosition()).getPhotoUri());
                Glide.with(holder.image.getContext()).load(uri).into(holder.image); // Glide를 사용하여 이미지 로드
            } else {
               holder.image.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

}
