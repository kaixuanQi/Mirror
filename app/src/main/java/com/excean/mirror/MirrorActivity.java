package com.excean.mirror;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.excean.middleware.ui.base.TitleActivity;
import com.excean.mirror.databinding.ActivityMirrorBinding;
import com.zero.support.common.AppGlobal;

public class MirrorActivity extends TitleActivity {
    public static void startActivity(Activity activity, PackageInfo info) {
        Intent intent = new Intent(activity, MirrorActivity.class);
        intent.putExtra("mirror", info);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_mirror_manager);
        ActivityMirrorBinding binding = setBindingContentView(R.layout.activity_mirror);
        binding.setViewModel(attachSupportViewModel(MirrorViewModel.class));
        if (binding.getViewModel().getPackageInfo() == null) {
            AppGlobal.sendMessage("应用不存在");
            finish();
        }
    }
}
