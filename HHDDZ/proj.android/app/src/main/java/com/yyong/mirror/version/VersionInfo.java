package com.yyong.mirror.version;

import com.google.gson.annotations.SerializedName;
import com.zero.support.common.toolbox.FileRequest;

import java.io.File;

public class VersionInfo {
    @SerializedName("content")
    public String content;
    @SerializedName("pkgname")
    public String packageName;
    @SerializedName("vername")
    public String versionName;
    @SerializedName("vercode")
    public long versionCode;
    @SerializedName("download")
    public String downloadUrl;
    @SerializedName("md5")
    public String md5;
    @SerializedName("size")
    public long size;
    @SerializedName("force")
    public boolean force;

    public transient FileRequest request;
    public transient boolean cellData;

    public FileRequest createFileRequest() {
        if (request!=null){
            return request;
        }
        File file = DownloadManger.getDefault().getDownloadFile(md5);
        file.getParentFile().mkdirs();
        request = new FileRequest.Builder().url(downloadUrl).output(file, 0)
                .md5(md5)
                .parallel(1)
                .contentLength(size).request();
        return request;
    }

    public FileRequest createFileRequest(boolean allowCellData) {
        if (cellData!=allowCellData){
            this.cellData = allowCellData;
            request = null;
        }
        return createFileRequest();
    }
}
