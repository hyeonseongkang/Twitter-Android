package com.example.twitter;

import android.graphics.Bitmap;

class MainData {
    private String key;
    private String user;
    private String content;
    private String photoKey;

    public MainData(){}

    public MainData(String key, String user, String content) {
        this.key = key;
        this.user = user;
        this.content = content;
    }

    public MainData(String key, String user, String content, String photoKey) {
        this.key = key;
        this.user = user;
        this.content = content;
        this.photoKey = photoKey;
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
}
