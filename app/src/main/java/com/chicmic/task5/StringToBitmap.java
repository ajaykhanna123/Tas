package com.chicmic.task5;

import android.graphics.Bitmap;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class StringToBitmap {
    public static String convertStringToBitmap(Bitmap bitmap, ImageView imageView) {
        imageView.setImageBitmap(bitmap);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] imageAsByte = byteArrayOutputStream.toByteArray();
        String rawString = Base64.encodeToString(imageAsByte, Base64.DEFAULT);
        return rawString;
    }
}
