package com.zero.support.common.component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

/**
 * 目前同CommonViewModel，CommonActivity强耦合
 */
@Deprecated
public class PermissionFragment extends CommonFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CommonActivity activity = (CommonActivity) requireActivity();
        RequestViewModel viewModel = activity.attachSupportViewModel(RequestViewModel.class);
        viewModel.obtainPermission().observe(activity, new Observer<PermissionModel>() {
            @Override
            public void onChanged(PermissionModel model) {
                requestPermissions(model.permissions(), 100);
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            getCurrent().dispatchResult(permissions, grantResults);
        }
    }

    private PermissionModel getCurrent() {
        CommonActivity activity = (CommonActivity) requireActivity();
        RequestViewModel viewModel = activity.peekViewModel(RequestViewModel.class);
        return viewModel.peekPermissionModel();
    }
}
