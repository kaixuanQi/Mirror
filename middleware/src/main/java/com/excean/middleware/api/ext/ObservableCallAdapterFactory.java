package com.excean.middleware.api.ext;




import com.zero.support.work.Observable;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class ObservableCallAdapterFactory extends CallAdapter.Factory {

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Class<?> cls  = getRawType(returnType);
        if (cls==null){
            return null;
        }
        if (cls!= Observable.class) {
            return null;
        }
        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Class<?> rawObservableType = getRawType(observableType);
//        if (rawObservableType != ApiResponse.class) {
//            throw new IllegalArgumentException("type must be a resource");
//        }
        if (! (observableType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("resource must be parameterized");
        }
//        Type bodyType = getParameterUpperBound(0, rawObservableType);
        return new ObservableCallAdapter<>(observableType);
    }
}