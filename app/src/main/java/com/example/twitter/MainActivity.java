package com.example.twitter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "MainActivity";
    public static final int PICK_IMAGE1 = 1;
    public static final int PICK_IMAGE2 = 2;
    public static final int PICK_IMAGE3 = 3;

    public static List<Uri> tempPhotoList = new ArrayList<>();
    public static List<Integer> tempPhotoListKey = new ArrayList<>();
    public int index;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = firebaseDatabase.getReference("Main");

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public static MainAdapter mainAdapter;

    private List<MainData> mainDataList = new ArrayList<>();
    private List<Uri> photoUriList = new ArrayList<>();

    private CircleImageView userProfile, photo;
    private RelativeLayout addPhotoButton, writeButton;

    private EditText content;
    private Bitmap photoBitmap = null;

    public static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        content = (EditText) findViewById(R.id.content);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        mainAdapter = new MainAdapter(mainDataList, photoUriList, firebaseUser.getEmail(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //delete
                        Object object = view.getTag();
                        if (object != null) {
                            final int position = (int) object;
                            Log.d(TAG, "DELETE");
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Delete");
                            builder.setMessage("Do you want delete this tweet?");
                            builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    myRef.child(mainDataList.get(position).getKey()).removeValue();
                                    mainDataList.remove(position);
                                    mainAdapter.notifyItemRemoved(position);
                                    mainAdapter.notifyDataSetChanged();
                                }
                            });

                            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });

                            builder.show();
                        }
                    }},
                new View.OnClickListener() {
                    @Override
                        public void onClick(View view) {
                            //modification
                            Object object = view.getTag();
                            if (object != null) {
                                final int position = (int) object;
                                Log.d(TAG, "MODIFICATION");
                                // database - modificationCheck == true -> 수정 활성화
                                myRef.child(mainDataList.get(position).getKey()).child("modificationCheck").setValue(true);

                            }
                        }
                    },
                new View.OnClickListener() {
                    @Override
                        public void onClick(View view) {
                            // cancel
                            Object object = view.getTag();
                            if (object != null) {
                                final int position = (int) object;
                                // database - modificationCheck == false -> 수정 비활성화
                                myRef.child(mainDataList.get(position).getKey()).child("modificationCheck").setValue(false);

                                // tempPhotoList, tempPhotoListKey에 position값이 있다면 제거하기
                                int index = MainActivity.tempPhotoListKey.indexOf(position);
                                if (index != -1) {
                                    tempPhotoList.remove(index);
                                    tempPhotoListKey.remove(index);
                                }

                            }
                        }
                },
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        // change photo
                        Object object = view.getTag();
                        if (object != null) {
                            final int position = (int) object;
                            pickImage(PICK_IMAGE3);
                            index = position;
                        }
                    }
                });
        recyclerView.setAdapter(mainAdapter);
        
        userProfile = (CircleImageView) findViewById(R.id.userProfile);
        userProfile.setEnabled(true);
        userProfile.setClickable(true);
        userProfile.setColorFilter(Color.parseColor("#1D9BF0"), PorterDuff.Mode.SRC_IN);
        userProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "set user Profile", Toast.LENGTH_SHORT).show();
                pickImage(PICK_IMAGE1);
            }
        });

        photo = (CircleImageView) findViewById(R.id.photo);
        addPhotoButton = (RelativeLayout) findViewById(R.id.addPhotoButton);
        addPhotoButton.setEnabled(true);
        addPhotoButton.setClickable(true);

        addPhotoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pickImage(PICK_IMAGE2);
            }
        });

        writeButton = (RelativeLayout) findViewById(R.id.writeButton);
        writeButton.setEnabled(true);
        writeButton.setClickable(true);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String setContent =  content.getText().toString();

                writeData(setContent);

            }
        });

        getData();

        // 권한 설정
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1002);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void pickImage(int imageCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        Log.d(TAG, String.valueOf(imageCode));
        startActivityForResult(intent, imageCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photoBitmap = null;
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();

            try {
                photoBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
                Log.d(TAG, String.valueOf(requestCode));
                if (requestCode == 1) {
                    userProfile.setColorFilter(null);
                    userProfile.setImageBitmap(photoBitmap);
                    photoBitmap = null;
                } else if (requestCode == 2) {
                    photo.setVisibility(View.VISIBLE);
                    photo.setImageBitmap(photoBitmap);
                    photoBitmap = null;
                } else {
                    // adapter Photo Change
                    // requestCode == 3 이라면 임시 저장소에 가져온 사진 Uri와 index값을 저장하여 adapter에서 사용할 수 있도록 한다.
                    tempPhotoList.add(uri);
                    tempPhotoListKey.add(index);
                    mainAdapter.notifyItemChanged(index);
                    photoBitmap = null;
                    index = -1;
                }

            }catch (IOException e) {

            }
        }
    }

    private void getData() {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mainDataList.clear();
                Log.d(TAG, "HELLO");
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    MainData mainData = childSnapshot.getValue(MainData.class);
                    mainDataList.add(mainData);
                }
                mainAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 앱 종료 시 모든 key에 대하여 modificationCheck == false로 하면서 다음에 앱이 실행 될 때 basicLayout이 보여지게 하기
        for (int i = 0; i < mainDataList.size(); i++) {
            myRef.child(mainDataList.get(i).getKey()).child("modificationCheck").setValue(false);
        }
    }

    private void writeData(String getContent) {
        String key = myRef.push().getKey();
        if (photoBitmap != null) {
            String photoKey = key + ".jpg";
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
                                myRef.child(key).setValue(new MainData(key, firebaseUser.getEmail(), getContent, photoKey, false, photoUri));
                            }
                        });

                    }
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "Upload is paused");
                }
            });
        } else {
            myRef.child(key).setValue(new MainData(key, firebaseUser.getEmail(), getContent, false));
        }

        content.setText("");
        photo.setImageBitmap(null);
        photo.setVisibility(View.GONE);
        photoBitmap = null;

        recyclerView.scrollToPosition(mainDataList.size());
    }

}