package com.yyong.mirror.version;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableInt;

import com.yyong.middleware.ChannelMeta;
import com.yyong.mirror.R;
import com.yyong.mirror.databinding.DialogUpgradeDownloadingBinding;
import com.zero.support.common.component.CommonActivity;
import com.zero.support.common.component.CommonDialog;
import com.zero.support.common.component.DialogModel;

public class ProgressDialog extends DialogModel {
    private VersionInfo versionInfo;
    private final ObservableInt progress = new ObservableInt();

    public ProgressDialog() {
        super("progress");
        this.versionInfo = versionInfo;
    }

    public void setProgress(int progress) {
        this.progress.set(progress);
    }


    public void setVersionInfo(VersionInfo versionInfo) {
        this.versionInfo = versionInfo;
    }


    @Override
    protected Dialog onCreateDialog(CommonActivity activity) {
        return new CommonDialog(activity) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                DialogUpgradeDownloadingBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_upgrade_downloading, null, false);
                setContentView(binding.getRoot());
                setCancelable(false);
                binding.setData(ProgressDialog.this);
            }


            @Override
            protected void performClicked(int which) {

            }
        };
    }

    public String getSize() {
        return Util.getTextSize(versionInfo.size);
    }

    public String getVersionName() {
        return ChannelMeta.getDefault().versionName;
    }

    public ObservableInt getProgress() {
        return progress;
    }
}
