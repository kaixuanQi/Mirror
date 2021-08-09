package com.zero.support.work;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Snapshot implements Parcelable {
    //代表任务状态
    private int status;
    private Progress progress = Progress.EMPTY_PROGRESS;
    private int errorCode;
    private String message;
    private Object data;

    public <T> T data() {
        return (T) data;
    }

    public Snapshot() {
    }

    public Snapshot(Snapshot snapshot) {
        if (snapshot != null) {
            this.status = snapshot.status;
            this.progress = snapshot.progress;
            this.errorCode = snapshot.errorCode;
            this.message = snapshot.message;
            this.data = snapshot.data;
        }
    }


    protected Snapshot(Parcel in) {
        status = in.readInt();
        progress = in.readParcelable(Progress.class.getClassLoader());
        errorCode = in.readInt();
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeParcelable(progress, flags);
        dest.writeInt(errorCode);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Snapshot> CREATOR = new Creator<Snapshot>() {
        @Override
        public Snapshot createFromParcel(Parcel in) {
            return new Snapshot(in);
        }

        @Override
        public Snapshot[] newArray(int size) {
            return new Snapshot[size];
        }
    };

    @Override
    public String toString() {
        return "Snapshot{" +
                "status=" + status +
                ", progress=" + progress +
                ", errorCode=" + errorCode +
                ", message='" + message + '\'' +
                '}';
    }

    public void readFromParcel(Parcel in) {
        status = in.readInt();
        if (in.readString() != null) {
            progress = Progress.CREATOR.createFromParcel(in);
        }
        errorCode = in.readInt();
        message = in.readString();
    }


    public int code() {
        return errorCode;
    }

    public String message() {
        return message;
    }

    public int status() {
        return status;
    }

    public Progress progress() {
        return progress;
    }

    public void finish(int code, String message) {
        this.status = Task.STATUS_COMPLETED;
        this.errorCode = code;
        this.message = message;
    }


    public boolean isEnqueued() {
        return status == Task.STATUS_ENQUEUED;
    }

    public boolean isRunning() {
        return status == Task.STATUS_RUNNING;
    }

    public boolean isFinished() {
        return status == Task.STATUS_COMPLETED;
    }

    public boolean isOK() {
        return errorCode == 0 && isFinished();
    }

    public Snapshot write(int status) {
        this.status = status;
        return this;
    }

    public Snapshot write(Progress progress) {
        this.progress = progress;
        return this;
    }

    public Snapshot write(Response<?> response) {
        errorCode = response.code();
        message = response.message();
        data = response.data();

        return this;
    }
}
