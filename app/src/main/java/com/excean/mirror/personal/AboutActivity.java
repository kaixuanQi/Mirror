package com.excean.mirror.personal;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.excean.middleware.ui.base.TitleActivity;
import com.excean.mirror.R;
import com.excean.mirror.databinding.ActivityAboutBinding;

public class AboutActivity extends TitleActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_about_us);

        ActivityAboutBinding binding = setBindingContentView(R.layout.activity_about);
        try {
            PackageInfo info =getPackageManager().getPackageInfo(getPackageName(),0);
            binding.version.setText(getString(R.string.about_version_name,info.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
}
