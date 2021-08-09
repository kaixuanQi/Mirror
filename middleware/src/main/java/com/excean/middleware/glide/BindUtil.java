package com.excean.middleware.glide;

import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

public class BindUtil {
    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }

    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, int resId) {
        view.setImageResource(resId);

    }

    @BindingAdapter("android:text")
    public static void setText(TextView view, int resId) {
        view.setText(resId);

    }
//    @BindingAdapter({"imageUrl", "placeHolder", "error"})
//    public static void loadImage(ImageView imageView, String url, Drawable holderDrawable, Drawable errorDrawable) {
//        Glide.with(imageView.getContext())
//                .load(url)
//                .placeholder(holderDrawable)
//                .error(errorDrawable)
//                .into(imageView);
//    }

    @BindingAdapter(value = {"imageModel", "placeHolder", "error"},requireAll = false)
    public static void loadImage(ImageView imageView, Object model, Drawable holderDrawable, Drawable errorDrawable) {
        Glide.with(imageView.getContext())
                .load(model)
                .placeholder(holderDrawable)
                .error(errorDrawable)
                .into(imageView);
    }





}
