package com.excean.mirror;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.databinding.Bindable;
import androidx.lifecycle.Observer;

import com.excean.middleware.ui.base.LocalDialogModel;
import com.excean.mirror.apps.AppPackageActivity;
import com.excean.mirror.vo.MirrorPackage;
import com.excean.virutal.api.virtual.Mirror;
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
        event = new ReceiverLiveEvent(Intent.ACTION_PACKAGE_REMOVED, Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_CHANGED, Intent.ACTION_PACKAGE_REMOVED);
        event.getIntentFilter().addDataScheme("package");

        event.observe(this, new androidx.lifecycle.Observer<Intent>() {
            @Override
            public void onChanged(Intent intent) {
                if (AppHolder.userGuide.getValue()) {
                    notifyDataSetChanged(null);
                }
            }
        });

        AppHolder.userGuide.asLive().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean) {
                    requestDialog(new LocalDialogModel.Builder().title(R.string.dialog_privacy_title)
                            .content(R.string.dialog_privacy_content).negative(R.string.dialog_privacy_negative).positive(R.string.dialog_privacy_positive)
                            .build()
                    ).click().observe(new com.zero.support.work.Observer<DialogClickEvent>() {
                        @Override
                        public void onChanged(DialogClickEvent dialogClickEvent) {
                            if (dialogClickEvent.isPositive()) {
                                AppHolder.userGuide.setValue(true);
                            } else {
                                ActivityManager.finishAll();
                                System.exit(0);
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

        for (PackageInfo pkg : packages) {
            if (!pkg.packageName.endsWith("mirror0")) {
                continue;
            }
            Mirror mirror = new Mirror(manager, pkg);
            if (mirror.getMirrorPackageInfo() == null) {
                continue;
            }
            list.add(new MirrorPackage(pkg, pkg.applicationInfo.loadLabel(manager).toString()));
        }
        return Response.success(list);
    }

    public void startAppPackages() {
        if (!AppHolder.userGuideProducerClick.getValue()) {
            AppHolder.userGuideProducerClick.setValue(true);
        }
        requireActivity().startActivity(new Intent(requireActivity(), AppPackageActivity.class));
    }
}
