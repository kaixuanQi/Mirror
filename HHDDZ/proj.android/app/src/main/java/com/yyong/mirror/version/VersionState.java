package com.yyong.mirror.version;

public class VersionState {
    public static final int STATE_PENDING = 0;
    public static final int STATE_DOWNLOADING = 1;
    public static final int STATE_FINISH = 2;
    private int state;
    private VersionInfo versionInfo;


    public VersionState(int state, VersionInfo versionInfo) {
        this.state = state;
        this.versionInfo = versionInfo;
    }

    @Override
    public String toString() {
        return "VersionState{" +
                "state=" + state +
                ", versionInfo=" + versionInfo +
                '}';
    }

    public int getState() {
        return state;
    }

    public VersionInfo getVersionInfo() {
        return versionInfo;
    }

    public boolean isPending() {
        return state == STATE_PENDING&&versionInfo!=null;
    }

    public boolean isDownloading() {
        return state == STATE_DOWNLOADING;
    }

    public boolean isFinish() {
        return state == STATE_FINISH;
    }
}
