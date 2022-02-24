package com.example.twitter;

import android.graphics.Bitmap;
import android.net.Uri;

class MainData {
    private String key;
    private String user;
    private String content;
    private String photoKey;
    private Boolean modificationCheck;
    private String photoUri;


    public MainData(){}

    public MainData(String key, String user, String content, Boolean modificationCheck) {
        this.key = key;
        this.user = user;
        this.content = content;
        this.modificationCheck = modificationCheck;
    }

    public MainData(String key, String user, String content, String photoKey, Boolean modificationCheck, String photoUri) {
        this.key = key;
        this.user = user;
        this.content = content;
        this.photoKey = photoKey;
        this.modificationCheck = modificationCheck;
        this.photoUri = photoUri;
    }

    public String getKey() {return key;}

    public String getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public String getPhotoKey() {
        return photoKey == null ? null : photoKey;
    }

    public Boolean getModificationCheck() { return modificationCheck; }

    public String getPhotoUri() { return photoUri; }
}
