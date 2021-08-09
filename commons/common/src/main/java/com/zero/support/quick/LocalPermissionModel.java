package com.zero.support.quick;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.zero.support.common.component.ActivityResultEvent;
import com.zero.support.common.component.ActivityResultModel;
import com.zero.support.common.component.CommonViewModel;
import com.zero.support.common.component.DialogClickEvent;
import com.zero.support.common.component.PermissionEvent;
import com.zero.support.common.component.PermissionModel;
import com.zero.support.work.Observer;

public class LocalPermissionModel extends PermissionModel {
    private String message;

    public LocalPermissionModel(String... permissions) {
        super(permissions);
    }

    public LocalPermissionModel content(String permission) {
        this.message = permission;
        return this;
    }

    @Override
    protected void onReceivePermissionEvent(final PermissionEvent event) {
        final CommonViewModel viewModel = requireViewModel();
        if (event.isPermanentlyDenied()) {
            requireViewModel().requestDialog(new SimpleDialogModel.Builder()
                    .name("权限说明")
                    .content(message).build()).click().observe(new Observer<DialogClickEvent>() {
                @Override
                public void onChanged(DialogClickEvent dialogClickEvent) {
                    dialogClickEvent.dismiss();
                    if (dialogClickEvent.isPositive()) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", viewModel.getApplication().getPackageName(), null));
                        viewModel.requestActivityResult(new ActivityResultModel(intent)).result().observe(new Observer<ActivityResultEvent>() {
                            @Override
                            public void onChanged(ActivityResultEvent activityResultEvent) {
                                viewModel.requestPermission(LocalPermissionModel.this);
                            }
                        });
                    } else {
                        deliveryEvent(event);
                    }
                }
            });
        } else if (!event.isGranted()) {
            requireViewModel().requestDialog(new SimpleDialogModel.Builder()
                    .name("权限引导")
                    .content(message).build()).click().observe(new Observer<DialogClickEvent>() {
                @Override
                public void onChanged(DialogClickEvent dialogClickEvent) {
                    dialogClickEvent.dismiss();
                    if (dialogClickEvent.isPositive()) {
                        requireViewModel().requestPermission(LocalPermissionModel.this);
                    } else {
                        deliveryEvent(event);
                    }
                }
            });
        } else {
            deliveryEvent(event);
        }
    }
}
