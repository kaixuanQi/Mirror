package com.excean.mirror.plugin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.excean.mirror.plugin.holder.R;
import com.excean.virtual.api.binder.Binder;
import com.excean.virutal.api.virtual.VirtualOperator;


public class MainActivity extends Activity {
    private Dialog dialog;

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
        if (dialog != null) {
            dialog.dismiss();
        }
        VirtualOperatorNative.INSTANCE.detach();
    }

    public void requestLaunchParams() {
        Intent intent = new Intent("com.excean.mirror.action.mirror");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setPackage(VirtualOperatorNative.INSTANCE.getMirror().getServicePackageName());
        Bundle bundle = new Bundle();
        bundle.putBinder("operator", Binder.asBinder(VirtualOperatorNative.INSTANCE, VirtualOperator.class));
        bundle.putInt("from", getIntent().getIntExtra("from", 0));
        bundle.putInt("userId", VirtualOperatorNative.INSTANCE.getMirror().userId);
        bundle.putString("mirrorPackage", VirtualOperatorNative.INSTANCE.getMirror().getMirrorPackageName());
        bundle.putLong("attribute", VirtualOperatorNative.INSTANCE.getMirror().attribute);
        intent.replaceExtras(bundle);
        try {
            startActivityForResult(intent, 100);
        } catch (Throwable e) {
            e.printStackTrace();
            obtainInstall().show();
        }

    }

    private Dialog obtainInstall() {
        if (dialog != null) {
            return dialog;
        }
        String message = "您尚未安装应用分身版，无法启动" + VirtualOperatorNative.INSTANCE.getMirror().name + "，请先安装后再尝试启动。";
        int theme;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            theme = android.R.style.Theme_Material_Light_Dialog_Alert;
        } else {
            theme = android.R.style.Theme_Holo_Dialog_NoActionBar;
        }
        dialog = new AlertDialog.Builder(this, theme)
                .setMessage(message)
                .setTitle("分身启动失败")
                .setCancelable(false)
                .setNegativeButton("暂不", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                }).setPositiveButton("立即安装", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestMarket();
                    }
                }).create();
        return dialog;
    }


    public void requestMarket() {
        Uri uri = Uri.parse("market://details?id=" + VirtualOperatorNative.INSTANCE.getMirror().getServicePackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (Throwable e) {

        }

    }
}