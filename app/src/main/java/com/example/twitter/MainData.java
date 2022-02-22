package com.example.twitter;

import android.graphics.Bitmap;

class MainData {
    private String user;
    private String content;
    private String photoKey;

    public MainData(){}

    public MainData(String user, String content, String photoKey) {
        this.user = user;
        this.content = content;
        this.photoKey = photoKey;
    }

    public String getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public String getPhotoKey() {
        return photoKey;
    }
}
