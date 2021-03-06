package com.example.twitter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

class FirebaseController {

    private static String TAG = "FirebaseController";

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = firebaseDatabase.getReference("Main");

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = mAuth.getCurrentUser();

    protected void writeData(String getKey, String getContent, Bitmap photoBitmap, boolean tempDelete, int index) {
        String key;
        if (getKey == null) key= myRef.push().getKey();
        else key= getKey;

        if (photoBitmap != null) {

            upLoadPhoto(key, photoBitmap, getContent, tempDelete, index);

            /*
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
            */

        } else {
            myRef.child(key).setValue(new MainData(key, firebaseUser.getEmail(), getContent, false));
        }
    }

    private void upLoadPhoto(String key, Bitmap photo, String content, boolean tempDelete, int index) {

        String photoKey = key + ".jpg";
        StorageReference storageReference = storageRef.child(photoKey);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
                            myRef.child(key).setValue(new MainData(key, firebaseUser.getEmail(), content, photoKey, false, photoUri));

                            if (tempDelete) {
                                MainActivity.tempPhotoList.remove(index);
                                MainActivity.tempPhotoListKey.remove(index);
                            }
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
    }

    protected void modificationState(String key, boolean state) {
        myRef.child(key).child("modificationCheck").setValue(state);
    }



}
