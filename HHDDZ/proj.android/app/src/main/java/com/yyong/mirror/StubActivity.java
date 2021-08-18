package com.yyong.mirror;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.yyong.middleware.ui.base.LocalDialogModel;
import com.yyong.virtual.api.binder.Binder;
import com.yyong.virutal.api.virtual.ActivityLaunchCallback;
import com.yyong.virutal.api.virtual.PluginManagerWrapper;
import com.yyong.virutal.api.virtual.VirtualOperator;
import com.zero.support.common.component.CommonActivity;
import com.zero.support.common.component.DialogModel;
import com.zero.support.common.component.RequestViewModel;

public class StubActivity extends CommonActivity {
    private static ActivityLaunchCallback callback = new ActivityLaunchCallback() {
        @Override
        public void onLaunch(String packageName, int from, int result) {
            if (result >= 0) {
                BIHelper.reportLaunchFinish(packageName, from, result);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IBinder binder = getIntent().getExtras().getBinder("operator");
        VirtualOperator operator = Binder.asInterface(binder, VirtualOperator.class);
        if (isMissingMirror()) {
            requestOpenMarket(operator);
        } else {
            String pkg = getMirrorPackage();
            String[] paths = null;
            long flag = AppHolder.getVirtualAttribute(getMirrorPackage());
            if (TextUtils.equals(pkg, "com.tencent.mm")) {
                flag |= PluginManagerWrapper.CAP_ISOLATE_SYSTEM_SETTINGS;
                paths = new String[]{"tencent", "Tencent"};
            }
            reply(flag, paths);
        }

    }

    public void reply(long flag, String[] paths) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putLong("attribute", flag);
        bundle.putStringArray("paths", paths);
        bundle.putBinder("binder", Binder.asBinder(callback, ActivityLaunchCallback.class));
        intent.replaceExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    private void requestOpenMarket(VirtualOperator operator) {
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

    public boolean isMissingMirror() {
        return getIntent().getBooleanExtra("missingMirror", false);
    }
}
