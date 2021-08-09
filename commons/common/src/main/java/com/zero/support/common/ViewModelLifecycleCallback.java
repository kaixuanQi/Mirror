package com.zero.support.common;

import com.zero.support.common.component.CommonActivity;
import com.zero.support.common.component.CommonViewModel;

public interface ViewModelLifecycleCallback {
    void onCreateViewModel(CommonViewModel viewModel);

    void onAttachActivity(CommonViewModel viewModel, CommonActivity activity);

    void onDetachActivity(CommonViewModel viewModel, CommonActivity activity);

    void onClear(CommonViewModel viewModel);
}
