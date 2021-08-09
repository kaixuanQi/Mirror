package com.excean.mirror;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;

import com.excean.middleware.ui.base.LocalDialogModel;
import com.excean.mirror.producer.MirrorProducerActivity;
import com.excean.virutal.api.virtual.Mirror;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.component.ActivityResultEvent;
import com.zero.support.common.component.ActivityResultModel;
import com.zero.support.common.component.CommonViewModel;
import com.zero.support.common.component.DialogClickEvent;
import com.zero.support.work.Observer;

public class MirrorViewModel extends CommonViewModel {
    private PackageInfo packageInfo;
    private String name;
    private String version;
    private Mirror mirror;

    @Override
    protected void onViewModelCreated() {
        super.onViewModelCreated();
    }

    @Override
    protected void onAttach(Activity activity) {
        super.onAttach(activity);
        packageInfo = activity.getIntent().getParcelableExtra("mirror");
        version = activity.getString(R.string.about_version_name, packageInfo.versionName);
        PackageManager pm = activity.getPackageManager();
        name = packageInfo.applicationInfo.loadLabel(activity.getPackageManager()).toString();
        mirror = new Mirror(pm, packageInfo);
    }

    public String getName() {
        return name;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public String getVersion() {
        return version;
    }

    public void onClickLauncher(View view) {
        Intent intent = requireActivity().getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
        requireActivity().startActivity(intent);
    }

    public void onClickFix(View view) {
        MirrorProducerActivity.startActivity(requireActivity(), mirror.getMirrorPackageInfo(),true);
    }

    public void onClickUninstall(View view) {
        requestDialog(new LocalDialogModel.Builder()
                .content(R.string.mirror_uninstall_warning)
                .negative(R.string.mirror_uninstall_negative)
                .positive(R.string.mirror_uninstall_positive)
                .build()).click().observe(new Observer<DialogClickEvent>() {
            @Override
            public void onChanged(DialogClickEvent dialogClickEvent) {
                if (dialogClickEvent.isNegative()) {
                    requestUninstall();
                } else {

                }
                dialogClickEvent.dismiss();
            }
        });
    }

    private void requestUninstall() {
        Uri uri = Uri.parse("package:" + packageInfo.packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        requestActivityResult(new ActivityResultModel(intent)).result().observe(new Observer<ActivityResultEvent>() {
            @Override
            public void onChanged(ActivityResultEvent event) {
                try {
                    if (requireActivity().getPackageManager().getPackageInfo(packageInfo.packageName, PackageManager.GET_META_DATA) == null) {

                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                requireActivity().finish();
            }
        });

    }
}
