package com.excean.middleware.api.internal;


import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.excean.middleware.api.DeviceUser;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 为每个请求增加deviceUser
 */
public class DeviceUserHeaderInterceptor implements Interceptor {
    String deviceUser;

    public DeviceUserHeaderInterceptor() {
        deviceUser = new Gson().toJson(new DeviceUser());
    }

    @NonNull
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("device-user", new String(Base64.encode(deviceUser.getBytes(), Base64.NO_WRAP)));

        return chain.proceed(builder.build());
    }
}