package com.yyong.mirror.version;

import android.app.Application;

import com.zero.support.common.AppGlobal;
import com.zero.support.common.toolbox.FileDownloadTask;
import com.zero.support.common.toolbox.FileRequest;
import com.zero.support.work.AppExecutor;
import com.zero.support.work.NetObservable;
import com.zero.support.work.Observer;
import com.zero.support.work.Progress;
import com.zero.support.work.Response;
import com.zero.support.work.SnapShotTask;
import com.zero.support.work.exception.DisableCellDataException;

import java.io.File;

@SuppressWarnings("ALL")
public class UpgradeRomTask extends SnapShotTask<UpgradeOperation, File> {
    /**
     * 获取下载信息
     */
    public static final int TYPE_FETCHING = 0;
    /**
     * 下载
     */
    public static final int TYPE_DOWNLOADING = 1;
    /**
     * 验证文件
     */
    public static final int TYPE_VERIFYING = 2;
    /**
     * 安装中
     */
    public static final int TYPE_INSTALLING = 3;


    private int downloadFlags;


    public int DOWNLOAD_WITH_CELL_DATA = 2;


    @Override
    protected File process(UpgradeOperation operation) throws Throwable {
        Application app = AppGlobal.getApplication();
        final Progress progress = new Progress();
        progress.type = TYPE_FETCHING;
        publishProgressChanged(new Progress(progress));
        Response<VersionInfo> response = operation.getVersionInfo(app);
        response.get();
        VersionInfo versionInfo = response.data();
        if (versionInfo == null) {
            return null;
        }
        File file = DownloadManger.getDefault().getDownloadFile(versionInfo.md5);
        file.getParentFile().mkdirs();
        FileRequest request = new FileRequest.Builder().url(versionInfo.downloadUrl).output(file, 0)
                .md5(versionInfo.md5)
                .parallel(1)
                .contentLength(versionInfo.size).request();
        progress.type = TYPE_DOWNLOADING;
        if (NetObservable.getNetWorkState() == NetObservable.NETWORK_MOBILE && !operation.isAllowCellData()) {
            throw new DisableCellDataException("disable cell data");
        }
        FileDownloadTask task = new FileDownloadTask();

        task.input(request).progress().observe(new Observer<Progress>() {
            @Override
            public void onChanged(Progress value) {
                progress.init(value);
                progress.type = TYPE_DOWNLOADING;
                publishProgressChanged(new Progress(progress));
            }
        });
        task.run(AppExecutor.current());
        task.awaitDone();
        task.getResult().printStackTrace();
        task.getResult().get();
        //single
        return file;
    }

}
