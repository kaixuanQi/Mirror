package com.excean.middleware.api.ext;


import com.zero.support.common.toolbox.FileRequest;
import com.zero.support.work.AppExecutor;
import com.zero.support.work.Observable;
import com.zero.support.work.Response;
import com.zero.support.work.WorkExceptionConverter;

import java.io.IOException;

import okhttp3.Request;
import retrofit2.Call;

public class CallObservable<T> extends Observable<Response<T>> {

    public CallObservable(final Call<Response<T>> call) {
        AppExecutor.async().execute(new Runnable() {
            @Override
            public void run() {
                Response<T> response;
                try {
                    retrofit2.Response<Response<T>> r = call.execute();

                    if (r.isSuccessful()) {
                        response = r.body();
                    } else {

                        response = Response.error(r.code(), r.message());
                    }
                } catch (Throwable e) {
                    e.printStackTrace();

                    response = Response.error(WorkExceptionConverter.convert(e), e.getMessage(), e);
                }
                setValue(response);
            }
        });
    }

    private String peekOrigin(Request origin){
        try {
            Request request = new Request.Builder().get().url(origin.url()).build();
            return FileRequest.DEFAULT_CLIENT.newCall(request).execute().body().string()+"  "+request.url();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
