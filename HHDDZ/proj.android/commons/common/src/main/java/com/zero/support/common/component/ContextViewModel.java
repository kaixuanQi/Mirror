package com.zero.support.common.component;

import android.annotation.SuppressLint;
import android.app.Application;

import com.zero.support.common.AppGlobal;
import com.zero.support.common.ViewModelManager;

public class ContextViewModel extends ObservableViewModel {
    private final Application application;
    @SuppressLint("StaticFieldLeak")
    private CommonActivity activity;
    private CommonFragment fragment;

    public ContextViewModel() {
        application = AppGlobal.getApplication();
    }

    void attach(CommonFragment fragment) {
        this.activity = (CommonActivity) fragment.requireActivity();
        this.fragment = fragment;
    }

    void attach(CommonActivity activity) {
        this.activity = activity;
    }

    public CommonFragment requireFragment() {
        final CommonFragment fragment = getFragment();
        if (fragment == null) {
            throw new IllegalStateException("Fragment " + this + " not attached to an activity.");
        }
        return fragment;
    }


    public CommonFragment getFragment() {
        return fragment;
    }

    public Application getApplication() {
        return application;
    }

    void detach() {
        fragment = null;
        activity = null;
    }

    public CommonActivity getActivity() {
        return activity;
    }

    public CommonActivity requireActivity() {
        if (activity == null) {
            throw new RuntimeException("activity is destroy");
        }
        return activity;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

    }
}
