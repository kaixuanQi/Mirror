package com.yyong.mirror.plugin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;

import com.yyong.mirror.plugin.holder.R;
import com.yyong.virtual.api.binder.Binder;
import com.yyong.virutal.api.virtual.ActivityLaunchCallback;
import com.yyong.virutal.api.virtual.VirtualOperator;


public class MainActivity extends Activity {
    private AlertDialog dialog;
    private AlertDialog sdcardDialog;
    private AlertDialog sourceDialog;
    private static final int request_sdcard = 101;
    private static final int request_source = 102;
    private static final int request_stub = 100;
    private boolean sdcardDenied;
    private boolean notFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VirtualOperatorNative.INSTANCE.attachActivity(this);
        if (VirtualOperatorNative.INSTANCE.getMirror().obb) {
            requestSdcard();
        } else {
            requestLaunchParams();
        }

    }

    private void requestSdcard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, android.os.Process.myPid(), Process.myUid()) != PackageManager.PERMISSION_GRANTED) {
            obtainSdcard().show();
        } else {
            requestInstallSource();
        }
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
        Intent intent = new Intent("com.yyong.mirror.action.mirror");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setPackage(VirtualOperatorNative.INSTANCE.getMirror().getServicePackageName());
        Bundle bundle = new Bundle();
        bundle.putBinder("operator", Binder.asBinder(VirtualOperatorNative.INSTANCE, VirtualOperator.class));
        bundle.putInt("from", getIntent().getIntExtra("from", 0));
        bundle.putInt("userId", VirtualOperatorNative.INSTANCE.getMirror().userId);
        bundle.putString("mirrorPackage", VirtualOperatorNative.INSTANCE.getMirror().getMirrorPackageName());
        bundle.putLong("attribute", VirtualOperatorNative.INSTANCE.getMirror().attribute);
        bundle.putString("name", VirtualOperatorNative.INSTANCE.getMirror().name);
        bundle.putString("mirrorName", VirtualOperatorNative.INSTANCE.getMirror().mirrorName);
        bundle.putBoolean("missingMirror", VirtualOperatorNative.INSTANCE.getMirror().getRemoteMirrorPackageInfo() == null);
        intent.replaceExtras(bundle);
        try {
            startActivityForResult(intent, request_stub);
        } catch (Throwable e) {
            e.printStackTrace();
            notFound = true;
            obtainInstall().show();
        }

    }

    private Dialog obtainInstallSource() {
        if (sourceDialog != null) {
            return sourceDialog;
        }
        String message = "??????????????????????????????????????????????????????????????????????????????????????????????????????";
        int theme;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            theme = android.R.style.Theme_Material_Light_Dialog_Alert;
        } else {
            theme = android.R.style.Theme_Holo_Dialog_NoActionBar;
        }
        sourceDialog = new AlertDialog.Builder(this, theme)
                .setMessage(message)
                .setTitle("??????")
                .setCancelable(false)
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setPositiveButton("????????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri packageURI = Uri.parse("package:" + getApplication().getPackageName());
                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                        startActivityForResult(intent, request_source);
                    }
                }).create();
        return sourceDialog;
    }

    private Dialog obtainSdcard() {
        if (sdcardDialog != null) {
            return sdcardDialog;
        }
        String message = "???????????????????????????????????????????????????????????????????????????????????????????????????";
        int theme;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            theme = android.R.style.Theme_Material_Light_Dialog_Alert;
        } else {
            theme = android.R.style.Theme_Holo_Dialog_NoActionBar;
        }
        sdcardDialog = new AlertDialog.Builder(this, theme)
                .setMessage(message)
                .setTitle("??????")
                .setCancelable(false)
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setPositiveButton("????????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (sdcardDenied) {
                                Uri packageURI = Uri.parse("package:" + getApplication().getPackageName());
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                startActivityForResult(intent, request_sdcard);
                            } else {
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, request_sdcard);
                            }
                        }
                    }
                }).create();
        return sdcardDialog;
    }

    private void requestInstallSource() {
        if (Build.VERSION.SDK_INT >= 29) {
            if (getPackageManager().canRequestPackageInstalls()) {
                requestLaunchParams();
            } else {
                obtainInstallSource().show();
            }
        } else {
            requestLaunchParams();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == request_sdcard) {
            if (grantResults != null && grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestInstallSource();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        sdcardDenied = !shouldShowRequestPermissionRationale(permissions[0]);
                    }
                    obtainSdcard().show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_source) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestInstallSource();
            }
        } else if (requestCode == request_sdcard) {
            requestSdcard();
        } else if (requestCode == request_stub) {
            if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
                final long attribute = data.getLongExtra("attribute", 0);
                final String[] paths = data.getStringArrayExtra("paths");
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    finish();
                    return;
                }
                AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        ActivityLaunchCallback callback = Binder.asInterface(bundle.getBinder("binder"), ActivityLaunchCallback.class);
                        VirtualOperatorNative.INSTANCE.startPlugin(attribute, paths, new ActivityLaunchCallback() {
                            @Override
                            public void onLaunch(String packageName, int from, int result) {
                                if (callback != null) {
                                    callback.onLaunch(packageName, from, result);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                });
            } else if (!notFound) {
                finish();
            }
        }

    }

    private Dialog obtainInstall() {
        if (dialog != null) {
            return dialog;
        }
        String message = "???????????????"+getResources().getString(R.string.mirror_host)+"???????????????" + VirtualOperatorNative.INSTANCE.getMirror().name + "????????????????????????????????????";
        int theme;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            theme = android.R.style.Theme_Material_Light_Dialog_Alert;
        } else {
            theme = android.R.style.Theme_Holo_Dialog_NoActionBar;
        }
        dialog = new AlertDialog.Builder(this, theme)
                .setMessage(message)
                .setTitle("??????????????????")
                .setCancelable(false)
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                }).setPositiveButton("????????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestMarket();
                        finish();
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

    @Override
    public void onBackPressed() {

    }
}