package com.yyong.mirror;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import androidx.databinding.Bindable;
import androidx.lifecycle.Observer;

import com.yyong.middleware.ui.base.LocalDialogModel;
import com.yyong.mirror.apps.AppPackageActivity;
import com.yyong.mirror.vo.MirrorPackage;
import com.yyong.virutal.api.virtual.Mirror;
import com.zero.support.common.ActivityManager;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.component.DataViewModel;
import com.zero.support.common.component.DialogClickEvent;
import com.zero.support.common.observable.ReceiverLiveEvent;
import com.zero.support.common.vo.Resource;
import com.zero.support.work.Response;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends DataViewModel<String, List<MirrorPackage>> {
    private ReceiverLiveEvent event;

    @Override
    protected void onViewModelCreated() {
        super.onViewModelCreated();
        event = new ReceiverLiveEvent(Intent.ACTION_PACKAGE_REMOVED, Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REPLACED);
        event.getIntentFilter().addDataScheme("package");

        event.observe(this, new androidx.lifecycle.Observer<Intent>() {
            @Override
            public void onChanged(Intent intent) {
                if (AppHolder.userGuide.getValue()) {
                    notifyDataSetChanged(null);
                }

                String action = intent.getAction();
                Uri uri = intent.getData();
                if (uri == null) {
                    return;
                }
                String pkg = uri.toString().replace("package:", "");
                if (!pkg.endsWith(".mirror0")) {
                    return;
                }
                pkg = pkg.replace(".mirror0", "");
                if (TextUtils.equals(action, Intent.ACTION_PACKAGE_REMOVED)) {
                    if (intent.getBooleanExtra(Intent.EXTRA_DATA_REMOVED, false)) {
                        BIHelper.reportPackageChanged(pkg, "removed", 2);
                    }
                } else if (TextUtils.equals(action, Intent.ACTION_PACKAGE_REPLACED)) {
                    if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                        BIHelper.reportPackageChanged(pkg, "replaced", 1);
                    }
                } else if (TextUtils.equals(action, Intent.ACTION_PACKAGE_ADDED)) {
                    if (!intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                        BIHelper.reportPackageChanged(pkg, "added", 0);
                    }
                }
            }
        });

        AppHolder.userGuide.asLive().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean) {
                    requestDialog(new LocalDialogModel.Builder().title(R.string.dialog_privacy_title)
                            .content(R.string.dialog_privacy_content).negative(R.string.dialog_privacy_negative).positive(R.string.dialog_privacy_positive)
                            .clickInterceptor(new LocalDialogModel.ClickInterceptor() {
                                @Override
                                public String intercept(String url) {
                                    if (TextUtils.equals("redirect://protocol", url)) {
                                        return AppHolder.PROTOCOL;
                                    } else if (TextUtils.equals("redirect://privacy", url)) {
                                        return AppHolder.PRIVACY;
                                    }
                                    return url;
                                }
                            })
                            .build()
                    ).click().observe(new com.zero.support.work.Observer<DialogClickEvent>() {
                        @Override
                        public void onChanged(DialogClickEvent dialogClickEvent) {
                            if (dialogClickEvent.isPositive()) {
                                AppHolder.userGuide.setValue(true);
                            } else {
                                ActivityManager.finishAll();
                            }
                            dialogClickEvent.dismiss();
                        }
                    });
                } else {
                    notifyDataSetChanged(null);
                }
            }
        });
        AppHolder.userGuideProducer.asLive().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean) {
                    requestDialog(new GuideDialogModel()).click().observe(new com.zero.support.work.Observer<DialogClickEvent>() {
                        @Override
                        public void onChanged(DialogClickEvent dialogClickEvent) {
                            dialogClickEvent.dismiss();
                            AppHolder.userGuideProducer.setValue(true);
                            if (dialogClickEvent.isPositive()) {
                                AppHolder.userGuideProducerClick.setValue(true);
                                startAppPackages();
                            }
                        }
                    });
                }
            }
        });


    }

    @Override
    protected void onResourceChanged(Resource<List<MirrorPackage>> resource) {
        super.onResourceChanged(resource);
        notifyPropertyChanged(BR.empty);
        notifyPropertyChanged(BR.prepared);
        notifyPropertyChanged(BR.progress);
    }

    @Bindable
    public boolean isEmpty() {
        Resource<List<MirrorPackage>> resource = resource().getValue();
        return resource != null && resource.isEmpty();
    }


    @Bindable
    public boolean isPrepared() {
        Resource<List<MirrorPackage>> resource = resource().getValue();
        return resource != null && resource.isSuccess() && !resource.isEmpty();
    }

    @Bindable
    public boolean isProgress() {
        Resource<List<MirrorPackage>> resource = resource().getValue();
        return resource != null && resource.isLoading();
    }

    @Override
    protected Response<List<MirrorPackage>> performExecute(String mirrors) {
        List<MirrorPackage> list = new ArrayList<>();
        Application app = AppGlobal.getApplication();
        PackageManager manager = app.getPackageManager();

        List<PackageInfo> packages = manager.getInstalledPackages(PackageManager.GET_META_DATA);
        List<String> names = new ArrayList<>();
        for (PackageInfo pkg : packages) {
            if (!pkg.packageName.endsWith("mirror0")) {
                continue;
            }
            Mirror mirror = new Mirror(manager, pkg);
            if (mirror.getMirrorPackageInfo() == null) {
                continue;
            }
            list.add(new MirrorPackage(pkg, pkg.applicationInfo.loadLabel(manager).toString()));
            names.add(pkg.packageName);
        }
        AppHolder.fetchVirtualAttributes(names);
        return Response.success(list);
    }

    public void startAppPackages() {
        if (!AppHolder.userGuideProducerClick.getValue()) {
            AppHolder.userGuideProducerClick.setValue(true);
        }
        requireActivity().startActivity(new Intent(requireActivity(), AppPackageActivity.class));
    }
}
