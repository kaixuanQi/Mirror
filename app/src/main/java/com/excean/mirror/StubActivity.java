package com.excean.mirror;

import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;

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
        String pkg = getCallingPackage();
        setResult(Activity.RESULT_OK);
        IBinder binder = getIntent().getExtras().getBinder("operator");
        VirtualOperator operator = Binder.asInterface(binder, VirtualOperator.class);
        operator.finishSplashActivity();
        AppExecutor.async().execute(new Runnable() {
            @Override
            public void run() {
                operator.startPlugin();
                AppExecutor.main().execute(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
