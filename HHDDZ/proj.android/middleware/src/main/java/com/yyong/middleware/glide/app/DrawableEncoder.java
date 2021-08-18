package com.yyong.middleware.glide.app;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Options;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DrawableEncoder implements Encoder<Drawable> {
    @Override
    public boolean encode(@NonNull Drawable data, @NonNull File file, @NonNull Options options) {
        Bitmap bitmap;
        if (data instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) data).getBitmap();
        } else {
            bitmap = Bitmap.createBitmap(data.getIntrinsicWidth(), data.getIntrinsicHeight(),
                    data.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            data.setBounds(0, 0, data.getIntrinsicWidth(), data.getIntrinsicHeight());
            data.draw(canvas);
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            return bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
