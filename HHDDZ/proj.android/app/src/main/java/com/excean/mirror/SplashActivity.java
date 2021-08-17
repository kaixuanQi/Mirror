package com.excean.mirror;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.excean.middleware.ui.base.BaseActivity;
import com.zero.support.work.AppExecutor;

public class SplashActivity extends BaseActivity implements Runnable {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppExecutor.getMainHandler().removeCallbacks(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppExecutor.getMainHandler().postDelayed(this,2000);
    }

    @Override
    public void run() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}
