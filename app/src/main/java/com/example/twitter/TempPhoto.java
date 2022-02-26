package com.example.twitter;

import android.graphics.Bitmap;

class TempPhoto {
    private Bitmap bitmap;
    private int position;

    public TempPhoto(Bitmap bitmap, int position) {
        this.bitmap = bitmap;
        this.position = position;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getPosition() {
        return position;
    }
}
