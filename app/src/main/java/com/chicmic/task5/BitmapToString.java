package com.chicmic.task5;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

public class BitmapToString {
    public static void convertStringToBitmap(String rawString, ImageView imageView) {

        byte[] imgBytes = Base64.decode(rawString, Base64.DEFAULT);
        Bitmap bitmap1 = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        imageView.setImageBitmap(bitmap1);
    }
}
