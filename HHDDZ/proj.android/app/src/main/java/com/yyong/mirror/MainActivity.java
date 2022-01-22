package com.yyong.mirror;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import com.yyong.middleware.ui.base.BaseActivity;
import com.yyong.mirror.databinding.ActivityMainBinding;
import com.yyong.mirror.personal.CenterActivity;
import com.yyong.mirror.version.UpgradeOperation;
import com.yyong.mirror.vo.MirrorPackage;
import com.zero.support.common.widget.recycler.CellAdapter;
import com.zero.support.common.widget.recycler.ItemViewHolder;
import com.zero.support.common.widget.recycler.OnItemClickListener;
import com.zero.support.common.widget.recycler.SimpleViewBound;
import com.zero.support.common.widget.recycler.divider.GridItemDecoration;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    CellAdapter adapter;
    MainViewModel viewModel;
    private boolean clickBack = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CenterActivity.class));
            }
        });
        viewModel = attachSupportViewModel(MainViewModel.class);
        binding.setViewModel(viewModel);
        adapter = new CellAdapter(viewModel);
//        adapter.getItems().setEmptyCell(new EmptyCell());
//        adapter.add(EmptyCell.class, new SimpleViewBound(BR.data, R.layout.view_bound_empty_package));
        adapter.add(MirrorPackage.class, new SimpleViewBound(BR.data, R.layout.view_bound_mirror_package));
        adapter.setEnableClick(true);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, ItemViewHolder holder) {
                MirrorActivity.startActivity(MainActivity.this, holder.<MirrorPackage>getItem().getPackageInfo(), BIHelper.LAUNCH_MAIN);
            }
        });
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        binding.recyclerView.addItemDecoration(new GridItemDecoration(this, 3, 8, false));
        viewModel.resource().observe(resource -> {
            if (resource.isLoading()) {

            } else if (resource.isSuccess()) {
                adapter.submitList(resource.data);
            } else if (resource.isError()) {

            }
        });
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.startAppPackages();
            }
        });
        AppHolder.userGuideProducerClick.asLive().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean) {
                    binding.fab.startRipple();
                } else {
                    binding.fab.stopRipple();
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (clickBack) {
            clickBack = false;
            UpgradeOperation.getDefault().checkUpdate();
        }
    }

    @Override
    public void onBackPressed() {
        clickBack = true;
        moveTaskToBack(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
