package com.zero.support.common.component;

import android.util.Log;
import android.util.SparseArray;

import com.zero.support.common.vo.Resource;
import com.zero.support.work.AppExecutor;
import com.zero.support.work.Observable;
import com.zero.support.work.Response;
import com.zero.support.work.SerialExecutor;
import com.zero.support.work.WorkExceptionConverter;

import java.util.Collection;
import java.util.concurrent.Executor;

public abstract class ResourceRequest<Param, Result> {
    private final Observable<Resource<Result>> resource = new Observable<>();

    private Result data;
    private final Executor executor = new SerialExecutor(1);


    public Observable<Resource<Result>> resource() {
        return resource;
    }

    protected void onResourceChanged(Resource<Result> resource) {

    }

    public void notifyDataSetChanged(Param param) {
        dispatchRefresh(param, data);
    }

    private void dispatchRefresh(Param param, Result data) {
        Resource<Result> r = Resource.loading(data);
        onResourceChanged(r);
        this.resource.setValue(r);
        getBackgroundExecutor().execute(() -> {
            Response<Result> response = null;
            try {
                response = performExecute(param);
            } catch (Exception e) {
                e.printStackTrace();
                response = Response.error(WorkExceptionConverter.convert(e),e);
            }
            Resource<Result> resource = covertToResource(response);
            AppExecutor.main().execute(() -> {
                onResourceChanged(resource);
                ResourceRequest.this.resource.setValue(resource);
            });
        });
    }

    private Resource<Result> covertToResource(Response<Result> response) {
        Resource<Result> resource;
        if (response.isSuccessful()) {
            resource = Resource.success(response.data());
        } else {
            resource = Resource.error(response);
        }
        return resource;
    }

    public boolean isEmptyData(Result data) {
        if (data == null) {
            return true;
        }
        if (data instanceof Collection) {
            return ((Collection<?>) data).size() == 0;
        }
        if (data instanceof SparseArray) {
            return ((SparseArray<?>) data).size() == 0;
        }
        return false;
    }

    public boolean hasCache() {
        return data != null;
    }

    protected Executor getBackgroundExecutor() {
        return executor;
    }

    protected abstract Response<Result> performExecute(Param param) throws Exception;
}
