package com.yyong.mirror.apps;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yyong.middleware.ui.base.TitleActivity;
import com.yyong.mirror.BIHelper;
import com.yyong.mirror.BR;
import com.yyong.mirror.MirrorActivity;
import com.yyong.mirror.R;
import com.yyong.mirror.databinding.ActivityPackagesBinding;
import com.yyong.mirror.producer.MirrorProducerActivity;
import com.yyong.mirror.vo.MirrorPackage;
import com.zero.support.common.widget.SlideBar;
import com.zero.support.common.widget.recycler.CellAdapter;
import com.zero.support.common.widget.recycler.ItemViewHolder;
import com.zero.support.common.widget.recycler.OnItemClickListener;
import com.zero.support.common.widget.recycler.SimpleViewBound;
import com.zero.support.common.widget.recycler.divider.HorizontalDividerItemDecoration;
import com.zero.support.common.widget.recycler.manager.StickyHeadersLinearLayoutManager;

public class AppPackageActivity extends TitleActivity {
    ActivityPackagesBinding binding;
    AppPackageViewModel viewModel;
    CellAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_app_packages);
        binding = setBindingContentView(R.layout.activity_packages);
        viewModel = attachSupportViewModel(AppPackageViewModel.class);
        binding.setViewModel(viewModel);
        adapter = new CellAdapter(viewModel);
        adapter.add(MirrorPackage.class, new SimpleViewBound(BR.data, BR.holder, R.layout.view_bound_app_pacakge));

        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, ItemViewHolder holder) {
                MirrorPackage info = holder.<MirrorPackage>getItem(MirrorPackage.class);
                if (info.isHasMirror()) {
                    MirrorActivity.startActivity(AppPackageActivity.this, info.getMirrorPackageInfo(), BIHelper.LAUNCH_MANAGER);
                } else {
                    BIHelper.reportRequestProduce(info.getPackageInfo().packageName);
                    MirrorProducerActivity.startActivity(AppPackageActivity.this, info.getPackageInfo(), false);
                    finish();
                }
            }
        });
        binding.recyclerView.setLayoutManager(new StickyHeadersLinearLayoutManager<>(this));
        binding.recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.parseColor("#dddddd"))
                .size(1)
                .build());
        binding.slideBar.setOnTouchLetterChangeListener(new SlideBar.OnTouchLetterChangeListener() {
            @Override
            public void onTouchLetterChange(boolean isTouch, String letter) {
                int index = viewModel.indexOf(adapter.getItems().values(), letter);
                LinearLayoutManager manager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
                if (manager!=null){
                    if (index!=-1){
                        manager.scrollToPositionWithOffset(index,0);
                    }
                }

            }
        });
        viewModel.resource().asLive().observe(this, resource -> {
            if (resource.isSuccess()) {
                adapter.submitList(resource.data);
            }
        });
    }

}
