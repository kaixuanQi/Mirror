package com.excean.mirror.producer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableBoolean;

import com.bumptech.glide.Glide;
import com.excean.mirror.R;
import com.excean.mirror.plugin.InstallProvider;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.component.ActivityResultEvent;
import com.zero.support.common.component.ActivityResultModel;
import com.zero.support.common.component.DataViewModel;
import com.zero.support.common.vo.Resource;
import com.zero.support.work.Observer;
import com.zero.support.work.Response;

import java.io.File;
import java.io.IOException;

public class MirrorProducerViewModel extends DataViewModel<PackageInfo, Producer> {
    private PackageInfo mirror;
    private String name;
    private String version;
    private final ObservableBoolean prepare = new ObservableBoolean();
    private boolean replace;

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
            notifyDataSetChanged(mirror);
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

    @Override
    protected void onResourceChanged(Resource<Producer> resource) {
        super.onResourceChanged(resource);
        if (resource.isSuccess()) {
            prepare.set(true);
            requestInstall(resource);
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
                Log.e("install", "onChanged: " + event);
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
    }


    @Override
    protected Response<Producer> performExecute(PackageInfo info) throws Exception {
        File icon = Glide.with(AppGlobal.getApplication())
                .downloadOnly()
                .load(info)
                .submit()
                .get();
        PackageManager manager = AppGlobal.getApplication().getPackageManager();
        String title = info.applicationInfo.loadLabel(manager).toString();

        Producer producer = ProducerManager.getDefault().getProducer(info.packageName);
        producer.produce(icon, title + "分身", 0, getAbiName());
        Thread.sleep(1000);
        return Response.success(producer);
    }

    String getAbiName() throws IOException {
        String name=getAbiNameTest();
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
