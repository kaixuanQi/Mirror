package com.excean.mirror;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.excean.mirror.apps.AppPackageActivity;
import com.zero.support.common.component.CommonActivity;
import com.zero.support.common.component.DialogModel;

public class GuideDialogModel extends DialogModel {

    @Override
    protected Dialog onCreateDialog(CommonActivity activity) {
        return new Dialog(activity) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.view_bound_guide);

                getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                getWindow().setBackgroundDrawable(null);
                View view = findViewById(R.id.guide);
                findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dispatchClickEvent(v, Dialog.BUTTON_POSITIVE);
                        dismiss();
                    }
                });
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dispatchClickEvent(v, Dialog.BUTTON_NEGATIVE);
                        dismiss();
                    }
                });
            }
        };
    }
}
