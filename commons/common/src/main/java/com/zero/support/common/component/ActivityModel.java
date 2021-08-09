package com.zero.support.common.component;

public class ActivityModel {
    private CommonViewModel viewModel;

    public final void attach(CommonViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public final void detach() {
//        this.viewModel = null;
    }

    public final CommonViewModel requireViewModel() {
        return viewModel;
    }


}
