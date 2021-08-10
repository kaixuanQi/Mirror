package com.excean.mirror.producer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.excean.middleware.ui.base.TitleActivity;
import com.excean.mirror.R;
import com.excean.mirror.databinding.ActivityProducerBinding;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.vo.Resource;
import com.zero.support.work.WorkErrorCode;

public class MirrorProducerActivity extends TitleActivity {
    public static void startActivity(Activity activity, PackageInfo info, boolean replace) {
        Intent intent = new Intent(activity, MirrorProducerActivity.class);
        intent.putExtra("mirror", info);
        intent.putExtra("replace", replace);
        activity.startActivity(intent);
    }

    private MirrorProducerViewModel viewModel;
    ActivityProducerBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_app_packages);
        binding = setBindingContentView(R.layout.activity_producer);
        viewModel = attachSupportViewModel(MirrorProducerViewModel.class);

        binding.setViewModel(viewModel);

        viewModel.resource().asLive().observe(this, new Observer<Resource<Producer>>() {
            @Override
            public void onChanged(Resource<Producer> resource) {
                if (resource.isLoading()) {
                    postStartAnimation();
                } else {
                    postStopAnimation();
                    if (resource.isError()) {
                        if (resource.code == WorkErrorCode.STORAGE_OVER_FLOW) {
                            AppGlobal.sendMessage("空间不足");
                        } else {
                            AppGlobal.sendMessage("网络连接失败，请检查网络设置");
                        }
                    }
                }
            }
        });

    }

    private void postStartAnimation() {
        binding.icon.post(new Runnable() {
            @Override
            public void run() {
                startAnimation();
            }
        });
    }

    private void postStopAnimation() {
        binding.icon.post(new Runnable() {
            @Override
            public void run() {
                stopAnimation();
            }
        });
    }


    private void startAnimation() {
        Animation animation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, binding.icon.getWidth() * 0.5f, binding.icon.getHeight() * 0.5f);
        animation.setDuration(1500);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setFillAfter(false);
        binding.icon.startAnimation(animation);
    }

    private void stopAnimation() {
        binding.icon.clearAnimation();
    }
}
