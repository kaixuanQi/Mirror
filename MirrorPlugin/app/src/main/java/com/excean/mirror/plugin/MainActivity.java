package com.excean.mirror.plugin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.excean.mirror.plugin.holder.R;
import com.excean.virtual.api.binder.Binder;
import com.excean.virutal.api.virtual.VirtualOperator;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VirtualOperatorNative.INSTANCE.attachActivity(this);
        requestLaunchParams();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VirtualOperatorNative.INSTANCE.detach();
    }

    public void requestLaunchParams() {
        Intent intent = new Intent("com.excean.mirror.action.mirror");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setPackage(VirtualOperatorNative.INSTANCE.getMirror().getServicePackageName());
        Bundle bundle = new Bundle();
        bundle.putBinder("operator", Binder.asBinder(VirtualOperatorNative.INSTANCE, VirtualOperator.class));
        intent.replaceExtras(bundle);
        try {
            startActivityForResult(intent, 100);
        } catch (Exception e) {
            e.printStackTrace();
            VirtualOperatorNative.INSTANCE.startPlugin();
        }

    }
}