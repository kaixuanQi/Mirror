package com.excean.mirror;

import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.excean.virtual.api.binder.Binder;
import com.excean.virutal.api.virtual.VirtualOperator;
import com.zero.support.common.component.CommonActivity;
import com.zero.support.work.AppExecutor;

public class StubActivity extends CommonActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setResult(Activity.RESULT_OK);
        IBinder binder = getIntent().getExtras().getBinder("operator");
        VirtualOperator operator = Binder.asInterface(binder, VirtualOperator.class);
        operator.finishSplashActivity();
        AppExecutor.async().execute(() -> {
            operator.startPlugin(AppHolder.getVirtualAttribute(getMirrorPackage()), result -> {
                if (result > 0) {
                    BIHelper.reportLaunchFinish(getMirrorPackage(), getLaunchFrom(), result);
                }
            });
            AppExecutor.main().execute(this::finish);
        });
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


}
