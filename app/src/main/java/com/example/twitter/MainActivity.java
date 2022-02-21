package com.example.twitter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "MainActivity";
    public static final int PICK_IMAGE = 1;
    public static final int PICK_IMAGE2 = 2;

    private FirebaseAuth mAuth;

    private CircleImageView userProfile, photo;
    private RelativeLayout addPhotoButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        
        userProfile = (CircleImageView) findViewById(R.id.userProfile);
        userProfile.setEnabled(true);
        userProfile.setClickable(true);
        userProfile.setColorFilter(Color.parseColor("#1D9BF0"), PorterDuff.Mode.SRC_IN);
        userProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "set user Profile", Toast.LENGTH_SHORT).show();
                pickImage(PICK_IMAGE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
                Log.d(TAG, String.valueOf(requestCode));
                if (requestCode == 1) {
                    userProfile.setColorFilter(null);
                    userProfile.setImageBitmap(bitmap);
                } else {
                    photo.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Hey");
                    photo.setImageBitmap(bitmap);
                }

            }catch (IOException e) {

            }

        }

    }
}