package com.excean.middleware.api.ext;


import android.util.Log;

import com.zero.support.work.Response;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;


/**
 * A Retrofit adapter that converts the Call into a LiveData of ApiResponse.
 * @param <R>
 */
public class ObservableCallAdapter<R> implements CallAdapter<Response<R>, CallObservable<R>> {
    private final Type responseType;
    public ObservableCallAdapter(Type responseType) {
        Log.d("main",responseType+"");
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public CallObservable<R> adapt(final Call<Response<R>> call) {
        return new CallObservable<>(call);
    }
}