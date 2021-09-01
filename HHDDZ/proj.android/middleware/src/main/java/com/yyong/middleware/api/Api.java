package com.yyong.middleware.api;

import android.util.Log;

import com.yyong.middleware.api.ext.GsonConverterFactory;
import com.yyong.middleware.api.ext.ObservableCallAdapterFactory;
import com.yyong.middleware.api.internal.DeviceUserHeaderInterceptor;

import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

public class Api {
    // 超时时间
    private static final long DEFAULT_TIMEOUT = 15L;
    private static final String BASE_API_URL = "https://bus.lightlivetv.com/splitmarker/";
    static WeakHashMap<Class<?>, Object> weakHashMap = new WeakHashMap<>();

    private static Retrofit retrofit;
    private static String accountId;

    static {
        Interceptor logger = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("Request", message);
            }
        });

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(logger)
                .addInterceptor(new DeviceUserHeaderInterceptor())
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_API_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new ObservableCallAdapterFactory())
                .build();
    }

    private Api() {

    }

    public void setAccountId(String id) {
        accountId = id;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> tClass) {
        T service = (T) weakHashMap.get(tClass);
        if (service == null) {
            service = retrofit.create(tClass);
            weakHashMap.put(tClass, service);
        }
        return service;
    }

}
