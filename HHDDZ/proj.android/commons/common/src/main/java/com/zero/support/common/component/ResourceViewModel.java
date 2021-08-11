package com.zero.support.common.component;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.zero.support.common.vo.Resource;
import com.zero.support.work.SerialExecutor;

import java.util.concurrent.Executor;

public class ResourceViewModel<T> extends CommonViewModel {
    private MutableLiveData<Resource<T>> resource = new MutableLiveData<>();
    private SerialExecutor executor = new SerialExecutor(1);
    private ObservableBoolean refreshing = new ObservableBoolean();

    public ResourceViewModel() {
        resource.observeForever(new Observer<Resource<T>>() {
            @Override
            public void onChanged(Resource<T> resource) {
                if (resource.isLoading()) {
                    refreshing.set(true);
                } else {
                    refreshing.set(false);
                }
                onSubmit(resource);
            }
        });
    }

    public void postResource(Resource<T> resource) {
        this.resource.postValue(resource);
    }

    public LiveData<Resource<T>> resource() {
        return resource;
    }

    public ObservableBoolean getRefreshing() {
        return refreshing;
    }

    protected void onSubmit(Resource<T> resource) {

    }

    public void notifyDataSetChanged() {
        postResource(Resource.loading((T) null));
        getBackgroundExecutor().execute(new Runnable() {
            @Override
            public void run() {
                refreshing.set(false);
                Resource<T> resource = onCreateResource();
                postResource(resource);
            }
        });
    }

    public Resource<T> onCreateResource() {
        return Resource.success(null);
    }

    public Executor getBackgroundExecutor() {
        return executor;
    }
}
