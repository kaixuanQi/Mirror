package com.excean.middleware.glide;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ResourceLoader;
import com.bumptech.glide.module.AppGlideModule;
import com.excean.middleware.glide.app.DrawableEncoder;
import com.excean.middleware.glide.app.PackageLoaderFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@GlideModule
public class GlideAppModule extends AppGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.prepend(PackageInfo.class, Drawable.class, new PackageLoaderFactory());
        registry.append(Drawable.class, new DrawableEncoder());
    }
}
