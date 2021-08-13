package com.excean.mirror;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.excean.middleware.ui.base.LocalDialogModel;
import com.excean.virtual.api.binder.Binder;
import com.excean.virutal.api.virtual.VirtualOperator;
import com.zero.support.common.component.CommonActivity;
import com.zero.support.common.component.DialogModel;
import com.zero.support.common.component.RequestViewModel;
import com.zero.support.work.AppExecutor;

public class StubActivity extends CommonActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setResult(Activity.RESULT_OK);
        IBinder binder = getIntent().getExtras().getBinder("operator");
        VirtualOperator operator = Binder.asInterface(binder, VirtualOperator.class);
        AppExecutor.async().execute(() -> {
            operator.startPlugin(AppHolder.getVirtualAttribute(getMirrorPackage()), result -> {
                operator.finishSplashActivity();
                if (result > 0) {
                    BIHelper.reportLaunchFinish(getMirrorPackage(), getLaunchFrom(), result);
                } else if (result == -100) {
                    AppExecutor.main().execute(this::requestOpenMarket);
                    return;
                }
                AppExecutor.main().execute(this::finish);
            });

        });
    }

    private void requestOpenMarket() {
        RequestViewModel viewModel = peekViewModel(RequestViewModel.class);
        DialogModel model = new LocalDialogModel.Builder().negative(R.string.dialog_install_negative)
                .positive(R.string.dialog_install_positive)
                .content(R.string.dialog_missing_mirror_content, getLaunchLabel())
                .build();
        viewModel.requestDialog(model).click().observe(event -> {
            if (event.isPositive()) {
                openMarket();
            }
            event.dismiss();
            finish();
        });

    }

    public void openMarket() {
        Uri uri = Uri.parse("market://details?id=" + getMirrorPackage());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (Throwable e) {

        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private String getMirrorPackage() {
        return getIntent().getStringExtra("mirrorPackage");
    }

    private int getLaunchFrom() {
        return getIntent().getIntExtra("from", 0);
    }

    private int getMirrorUserId() {
        return getIntent().getIntExtra("userId", 0);
    }

    public String getLaunchLabel() {
        return getIntent().getStringExtra("mirrorName");
    }

}
