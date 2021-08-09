package com.zero.support.common;

import com.zero.support.common.component.CommonActivity;
import com.zero.support.common.component.CommonViewModel;

import java.util.ArrayList;
import java.util.List;

public class ViewModelManager {
    private static final List<ViewModelLifecycleCallback> callbacks = new ArrayList<>();

    public static void registerViewModelLifecycleCallback(ViewModelLifecycleCallback callback) {
        callbacks.add(callback);
    }

    public static void unregisterViewModelLifecycleCallback(ViewModelLifecycleCallback callback) {
        callbacks.remove(callback);
    }

    public static void dispatchCreateViewModel(CommonViewModel viewModel) {
        for (ViewModelLifecycleCallback callback : callbacks) {
            callback.onCreateViewModel(viewModel);
        }
    }


    public static void dispatchAttachActivity(CommonViewModel viewModel, CommonActivity activity) {
        for (ViewModelLifecycleCallback callback : callbacks) {
            callback.onAttachActivity(viewModel, activity);
        }
    }

    public static void dispatchDetachActivity(CommonViewModel viewModel, CommonActivity activity) {
        for (ViewModelLifecycleCallback callback : callbacks) {
            callback.onDetachActivity(viewModel, activity);
        }
    }

    public static void dispatchClear(CommonViewModel viewModel) {
        for (ViewModelLifecycleCallback callback : callbacks) {
            callback.onClear(viewModel);
        }
    }
}
