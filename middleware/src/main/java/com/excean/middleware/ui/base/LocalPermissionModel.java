package com.excean.middleware.ui.base;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;


import com.excean.vphone.middleware.R;
import com.zero.support.common.component.ActivityResultEvent;
import com.zero.support.common.component.ActivityResultModel;
import com.zero.support.common.component.DialogClickEvent;
import com.zero.support.common.component.PermissionEvent;
import com.zero.support.common.component.PermissionModel;
import com.zero.support.work.Observer;

public class LocalPermissionModel extends PermissionModel {
    public LocalPermissionModel(String... permissions) {
        super(permissions);
    }

    @Override
    protected void onReceivePermissionEvent(final PermissionEvent event) {
        if (event.isPermanentlyDenied()) {
            requireViewModel().requestDialog(new LocalDialogModel.Builder().content(R.string.rationale_ask_again).build()).click().observe(new Observer<DialogClickEvent>() {
                @Override
                public void onChanged(DialogClickEvent dialogClickEvent) {
                    dialogClickEvent.dismiss();
                    if (dialogClickEvent.isPositive()) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", requireViewModel().getApplication().getPackageName(), null));
                        requireViewModel().requestActivityResult(new ActivityResultModel(intent)).result().observe(new Observer<ActivityResultEvent>() {
                            @Override
                            public void onChanged(ActivityResultEvent activityResultEvent) {
                                requireViewModel().requestPermission(LocalPermissionModel.this);
                            }
                        });
                    } else {
                        deliveryEvent(event);
                    }
                }
            });
        } else if (!event.isGranted()) {
            requireViewModel().requestDialog(new LocalDialogModel.Builder().content(R.string.rationale_ask).build()).click().observe(new Observer<DialogClickEvent>() {
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
