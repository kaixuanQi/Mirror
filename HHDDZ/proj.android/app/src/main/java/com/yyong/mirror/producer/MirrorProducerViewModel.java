package com.yyong.mirror.producer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableBoolean;

import com.bumptech.glide.Glide;
import com.yyong.middleware.api.Api;
import com.yyong.middleware.ui.base.LocalDialogModel;
import com.yyong.mirror.AppHolder;
import com.yyong.mirror.BIHelper;
import com.yyong.mirror.R;
import com.yyong.mirror.api.ApiService;
import com.yyong.mirror.api.VirtualAttribute;
import com.yyong.mirror.plugin.InstallProvider;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.component.ActivityResultEvent;
import com.zero.support.common.component.ActivityResultModel;
import com.zero.support.common.component.DataViewModel;
import com.zero.support.common.component.DialogClickEvent;
import com.zero.support.common.component.DialogModel;
import com.zero.support.common.component.PermissionEvent;
import com.zero.support.common.component.PermissionHelper;
import com.zero.support.common.component.PermissionModel;
import com.zero.support.common.vo.Resource;
import com.zero.support.work.Observer;
import com.zero.support.work.Response;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class MirrorProducerViewModel extends DataViewModel<PackageInfo, Producer> {
    private PackageInfo mirror;
    private String name;
    private String version;
    private final ObservableBoolean prepare = new ObservableBoolean();
    private final ObservableBoolean error = new ObservableBoolean();
    private boolean replace;
    private boolean denied;

    @Override
    protected void onViewModelCreated() {
        super.onViewModelCreated();
    }

    @Override
    protected void onAttach(Activity activity) {
        super.onAttach(activity);
        if (mirror == null) {
            mirror = activity.getIntent().getParcelableExtra("mirror");
            replace = activity.getIntent().getBooleanExtra("replace", false);
            version = activity.getString(R.string.about_version_name, mirror.versionName);
            name = mirror.applicationInfo.loadLabel(activity.getPackageManager()).toString();
            requestStorage();
        }
    }


    public String getName() {
        return name;
    }

    public PackageInfo getMirror() {
        return mirror;
    }

    public String getVersion() {
        return version;
    }

    public ObservableBoolean getPrepare() {
        return prepare;
    }

    public ObservableBoolean getError() {
        return error;
    }

    @Override
    protected void onResourceChanged(Resource<Producer> resource) {
        super.onResourceChanged(resource);
        if (resource.isLoading()) {
            BIHelper.reportProduceStart(mirror.packageName);
        } else if (resource.isSuccess()) {
            BIHelper.reportProduceFinish(mirror.packageName, resource);
            prepare.set(true);
            requestInstall(resource);
        } else if (resource.isError()) {
            error.set(true);
            BIHelper.reportProduceFinish(mirror.packageName, resource);
        }
    }

    private void requestStorage() {
        if (!PermissionHelper.hasPermissions(AppGlobal.getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            requestDialog(new LocalDialogModel.Builder()
                    .positive(denied ? R.string.dialog_permission_settings_positive : R.string.dialog_permission_positive)
                    .negative(R.string.dialog_install_negative)
                    .content("需要授权访问“存储权限”，否则将导致应用资料缺失，分身无法正常运行。")
                    .build()).click().observe(new Observer<DialogClickEvent>() {
                @Override
                public void onChanged(DialogClickEvent event) {
                    if (event.isPositive()) {
                        if (denied) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", requireActivity().getPackageName(), null));
                            requestActivityResult(new ActivityResultModel(intent)).result().observe(new Observer<ActivityResultEvent>() {
                                @Override
                                public void onChanged(ActivityResultEvent event) {
                                    requestStorage();
                                }
                            });
                        } else {
                            requestPermission(new PermissionModel(Manifest.permission.READ_EXTERNAL_STORAGE)).result().observe(new Observer<PermissionEvent>() {
                                @Override
                                public void onChanged(PermissionEvent event) {
                                    if (event.isGranted()) {
                                        requestInstallWrapper();
                                    } else if (event.isPermanentlyDenied()) {
                                        denied = true;
                                        requestStorage();
                                    }else {
                                        requestStorage();
                                    }
                                }
                            });
                        }
                        event.dismiss();
                    } else {
                        event.dismiss();
                        requireActivity().finish();
                    }
                }
            });
        } else {
            requestInstallWrapper();
        }

    }

    private void requestInstallWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (getApplication().getPackageManager().canRequestPackageInstalls()) {
                notifyDataSetChanged(mirror);
            } else {
                DialogModel model = new LocalDialogModel.Builder()
                        .content(R.string.dialog_permission_install_content)
                        .positive(R.string.dialog_permission_positive)
                        .negative(R.string.dialog_install_negative)
                        .build();
                model.click().observe(new Observer<DialogClickEvent>() {
                    @Override
                    public void onChanged(DialogClickEvent event) {
                        if (event.isPositive()) {
                            Uri packageURI = Uri.parse("package:" + getApplication().getPackageName());
                            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                            requestActivityResult(new ActivityResultModel(intent)).result().observe(new Observer<ActivityResultEvent>() {
                                @Override
                                public void onChanged(ActivityResultEvent event) {
                                    if (getApplication().getPackageManager().canRequestPackageInstalls()) {
                                        model.dismiss();
                                        notifyDataSetChanged(mirror);
                                    }
                                }
                            });
                        } else {
                            requireActivity().finish();
                            event.dismiss();
                        }

                    }
                });
                requestDialog(model);
            }
        } else {
            notifyDataSetChanged(mirror);
        }
    }

    private void requestInstall(Resource<Producer> resource) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = InstallProvider.getUriForFile(resource.data.getTarget());
        } else {
            uri = Uri.fromFile(resource.data.getTarget());
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        requestActivityResult(new ActivityResultModel(intent)).result().observe(new Observer<ActivityResultEvent>() {
            @Override
            public void onChanged(ActivityResultEvent event) {
                if (replace) {
                    requireActivity().setResult(event.resultCode());
                    requireActivity().finish();
                    return;
                }
                PackageInfo target = getTargetPackageInfo(resource.data.getTargetPackageName());
                if (target != null) {
                    requireActivity().setResult(Activity.RESULT_OK);
                    requireActivity().finish();
                } else {
                    AppGlobal.sendMessage("安装失败");
                }
            }
        });
    }

    private PackageInfo getTargetPackageInfo(String packageName) {
        try {
            PackageInfo info = AppGlobal.getApplication().getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
            return info;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onClickInstall(View view) {
        requestInstall(resource().getValue());
        BIHelper.reportRequestInstall(mirror.packageName);
    }

    public void onClickRetry(View view) {
        error.set(false);
        notifyDataSetChanged(mirror);
    }

    @Override
    protected Response<Producer> performExecute(PackageInfo info) throws Exception {
        Response<List<VirtualAttribute>> result = Api.getService(ApiService.class).requestVirtualAttribute(Collections.singletonList(info.packageName)).getFuture().getValue();
        if (result.isSuccessful()) {
            AppHolder.updateVirtualAttribute(result.data());
        }
        File icon = Glide.with(AppGlobal.getApplication())
                .downloadOnly()
                .load(info)
                .submit()
                .get();
        PackageManager manager = AppGlobal.getApplication().getPackageManager();
        String title = info.applicationInfo.loadLabel(manager).toString();

        Producer producer = ProducerManager.getDefault().getProducer(info.packageName);
        String obbDir = AppGlobal.getApplication().getObbDir().getAbsolutePath();
        obbDir = obbDir.replace(AppGlobal.getApplication().getPackageName(),info.packageName);
        File file = new File(obbDir,"main."+info.versionCode+"."+info.packageName+".obb");
        boolean hasObb = file.exists();
        producer.produce(icon, title + "分身", title, 0, getAbiName(),hasObb);
        return Response.success(producer);
    }

    String getAbiName() throws IOException {
        String name = getAbiNameTest();
        Log.e("mirror", "getAbiName: " + name);
        return name;
    }

    private String getAbiNameTest() throws IOException {
        String name = new File(mirror.applicationInfo.nativeLibraryDir).getCanonicalFile().getName();
        if (name.contains("arm")) {
            if (name.contains("64")) {
                return "arm64-v8a";
            } else {
                return "armeabi-v7a";
            }
        } else {
            if (name.contains("64")) {
                return "x86_64";
            } else {
                return "x86";
            }
        }
    }
}
