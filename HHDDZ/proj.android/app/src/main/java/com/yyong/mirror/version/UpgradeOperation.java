package com.yyong.mirror.version;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.yyong.middleware.api.Api;
import com.yyong.mirror.api.ApiService;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.toolbox.FileDownloadTask;
import com.zero.support.common.util.Singleton;
import com.zero.support.work.NetObservable;
import com.zero.support.work.Observable;
import com.zero.support.work.Observer;
import com.zero.support.work.Response;
import com.zero.support.work.Snapshot;

import java.io.File;

public class UpgradeOperation {
    private final static Singleton<UpgradeOperation> singleton = new Singleton<UpgradeOperation>() {
        @Override
        protected UpgradeOperation create() {
            return new UpgradeOperation(DownloadManger.getDefault());
        }
    };

    public static UpgradeOperation getDefault() {
        return singleton.get();
    }

    private final Observable<Snapshot> snapshot = new Observable<>();
    private DownloadManger manger;
    private volatile VersionInfo versionInfo;
    private final Observable<Response<VersionInfo>> version = new Observable<>();

    //    private final NetworkWatcher networkWatcher = new NetworkWatcher();
    private final VersionWatcher versionWatcher = new VersionWatcher();
    private final UpgradeProgress upgradeProgress = new UpgradeProgress();
    private final VersionObserver versionObserver = new VersionObserver();

    private boolean allowCellData;
    private volatile boolean fetching;


    private final Observable<VersionState> versionState = new Observable<>();


    public Observable<Response<VersionInfo>> checkUpdate() {
        if (fetching || versionInfo != null) {
            return version;
        }
        fetching = true;
        getVersionInfoInBackground(AppGlobal.getApplication(), versionObserver);
        return version;
    }


    public void enableCellData(boolean allowCellData) {
        this.allowCellData = allowCellData;
    }

    public boolean isAllowCellData() {
        return allowCellData;
    }

    public UpgradeOperation(DownloadManger manger) {
        this.manger = manger;
    }

    public Observable<Snapshot> snapshot() {
        return snapshot;
    }

    public Observable<VersionState> versionState() {
        return versionState;
    }


    public void requestUpgrade(Activity activity,boolean allowCellData) {
        this.allowCellData = allowCellData;
        requestContinued(activity);
    }

    public void requestContinued() {
        manger.getQueue().opt(versionInfo.createFileRequest(allowCellData)).snapshot().observe(upgradeProgress);
    }

    public void requestContinued(Activity activity){
//        activity.startService(new Intent(activity, DownloadService.class));
        requestContinued();
    }


    public void requestStopUpgrade() {
        FileDownloadTask task = manger.getQueue().get(versionInfo.createFileRequest(allowCellData));
        if (task != null) {
            task.cancel(true);
        }
    }


    public Observable<Response<VersionInfo>> version() {
        return version;
    }


//    public void waitingWifiAutoDownload() {
//        AppGlobal.netStatus().observe(networkWatcher);
//    }

    private void autoCheckUpdate() {
        AppGlobal.netStatus().observe(versionWatcher);
    }

    public void clearVersion() {
        fetching = false;
        versionInfo = null;
        version.setValue(Response.success(null));
    }



    private class VersionWatcher implements Observer<Integer> {

        @Override
        public void onChanged(Integer integer) {

            integer = NetObservable.getNetWorkState();
            if (integer != NetObservable.NETWORK_NONE) {
                AppGlobal.netStatus().remove(this);
                checkUpdate();
            }
        }
    }

    private class VersionObserver implements Observer<Response<VersionInfo>> {

        @Override
        public void onChanged(Response<VersionInfo> response) {
            fetching = false;
            if (!response.isSuccessful()) {
                autoCheckUpdate();
            } else {
                if (response.data() != null && response.data().downloadUrl == null) {
                    response = Response.success(null);
                }
                versionInfo = response.data();
                version.setValue(response);
                versionState.setValue(new VersionState(VersionState.STATE_PENDING, versionInfo));
            }

        }
    }

    private class UpgradeProgress implements Observer<Snapshot> {
        private int type = -1;
        private Application app = AppGlobal.getApplication();

        @Override
        public void onChanged(Snapshot snapshot) {

            if (snapshot.isEnqueued()) {

                versionState.setValue(new VersionState(VersionState.STATE_DOWNLOADING, versionInfo));
            } else if (snapshot.isFinished()) {

                versionState.setValue(new VersionState(VersionState.STATE_FINISH, versionInfo));
                if (snapshot.isOK()) {
//                    versionInfo = null;
//                    version.setValue(Response.<VersionInfo>success(null));
//                    upgrade.setValue(false);
                }
            } else if (snapshot.isRunning()) {
                int t = snapshot.progress().getType();
                if (t != type) {
                    type = snapshot.progress().getType();
                }
            }
            UpgradeOperation.this.snapshot.setValue(snapshot);
        }
    }

    public VersionInfo getVersionInfo() {
        return versionInfo;
    }

    public Response<VersionInfo> getVersionInfo(Context app) {
        if (versionInfo != null) {
            return Response.success(versionInfo);
        }
        VersionInfo romDetail = null;
        romDetail = getLocalVersionInfo(app, app.getObbDir());
        if (romDetail == null) {
            romDetail = getLocalVersionInfo(app, app.getExternalCacheDir().getParentFile());
        }
        if (romDetail == null) {
            Response<VersionInfo> response = Api.getService(ApiService.class).requestUpdate().getFuture().getValue();
            return response;
        }
        return Response.success(romDetail);
    }


    public void getVersionInfoInBackground(Context app, Observer<Response<VersionInfo>> observer) {
        if (versionInfo != null) {
            observer.onChanged(Response.success(versionInfo));
            return;
        }
        VersionInfo romDetail = null;
        romDetail = getLocalVersionInfo(app, app.getObbDir());
        if (romDetail == null) {
            romDetail = getLocalVersionInfo(app, app.getExternalCacheDir().getParentFile());
        }
        if (romDetail == null) {
            Api.getService(ApiService.class).requestUpdate().asLive().observeForever(new androidx.lifecycle.Observer<Response<VersionInfo>>() {
                @Override
                public void onChanged(Response<VersionInfo> response) {
                    observer.onChanged(response);
                }
            });
        } else {
            observer.onChanged(Response.success(romDetail));
        }

    }


    private VersionInfo getLocalVersionInfo(Context context, File root) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            File file = new File(root, "main" + "." + packageInfo.versionCode + "." + packageInfo.packageName + ".obb");
            if (file.exists()) {
                VersionInfo detail = new VersionInfo();
                detail.downloadUrl = Uri.fromFile(root).toString();
                detail.size = root.length();
                return detail;
            }
            file = new File(root, "rootfs.zip");
            if (file.exists()) {
                VersionInfo detail = new VersionInfo();
                detail.downloadUrl = Uri.fromFile(root).toString();
                detail.size = root.length();
                return detail;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
