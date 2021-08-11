package com.zero.support.common.component;

import com.zero.support.common.vo.Resource;
import com.zero.support.work.Observable;
import com.zero.support.work.Response;

public abstract class DataViewModel<Param, Result> extends CommonViewModel {
    private final ResourceRequest<Param, Result> request = new ResourceRequest<Param, Result>() {
        @Override
        protected Response<Result> performExecute(Param param) throws Exception{
            return DataViewModel.this.performExecute(param);
        }

        @Override
        protected void onResourceChanged(Resource<Result> resource) {
            DataViewModel.this.onResourceChanged(resource);
        }
    };


    public Observable<Resource<Result>> resource() {
        return request.resource();
    }

    protected void onResourceChanged(Resource<Result> resource) {

    }

    public final void performResource(Resource<Result> resource, int loading, int success, int error) {
        if (resource.isLoading()) {
            postLoading(loading);
        } else if (resource.isSuccess()) {
            postSuccess(success);
        } else if (resource.isError()) {
            postError(error);
        }
    }

    public void notifyDataSetChanged(Param param) {
        request.notifyDataSetChanged(param);
    }


    protected abstract Response<Result> performExecute(Param param) throws Exception;
}
