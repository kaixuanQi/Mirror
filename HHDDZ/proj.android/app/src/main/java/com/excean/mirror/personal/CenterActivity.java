package com.excean.mirror.personal;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.excean.middleware.ui.base.TitleActivity;
import com.excean.middleware.ui.base.WebViewActivity;
import com.excean.mirror.AppHolder;
import com.excean.mirror.BR;
import com.excean.mirror.R;
import com.excean.mirror.databinding.ActivityCenterBinding;
import com.excean.mirror.databinding.ActivityPackagesBinding;
import com.excean.mirror.personal.vo.ManagerItem;
import com.excean.mirror.version.DownloadManger;
import com.excean.mirror.version.UpgradeOperation;
import com.zero.support.common.component.RequestViewModel;
import com.zero.support.common.widget.recycler.CellAdapter;
import com.zero.support.common.widget.recycler.SimpleViewBound;
import com.zero.support.common.widget.recycler.divider.GridItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class CenterActivity extends TitleActivity {
    ActivityCenterBinding binding;

    CellAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_manager_center);
        binding = setBindingContentView(R.layout.activity_center);
        adapter = new CellAdapter(peekViewModel(RequestViewModel.class));
        adapter.setEnableClick(true);
        adapter.add(ManagerItem.class, new SimpleViewBound(BR.data, BR.holder, R.layout.view_bound_manage_item));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        binding.recyclerView.addItemDecoration(new GridItemDecoration(this, 3, 20, true));
        adapter.submitList(buildList());
    }

    private List<ManagerItem> buildList() {
        List<ManagerItem> list = new ArrayList<>();
        list.add(new ManagerItem(R.string.title_question, R.drawable.ic_question, v->startActivity(new Intent(CenterActivity.this,QuestionActivity.class))));
        list.add(new ManagerItem(R.string.title_feedback, R.drawable.ic_feedback, v -> startActivity(new Intent(CenterActivity.this, FeedbackActivity.class))));
        list.add(new ManagerItem(R.string.title_check_update, R.drawable.ic_check_update, v-> UpgradeOperation.getDefault().checkUpdate()));
        list.add(new ManagerItem(R.string.title_privacy, R.drawable.ic_privacy, (v -> WebViewActivity.open(CenterActivity.this, AppHolder.PRIVACY))));
        list.add(new ManagerItem(R.string.title_protocol, R.drawable.ic_procotol, (v ->WebViewActivity.open(CenterActivity.this, AppHolder.PROTOCOL))));
        list.add(new ManagerItem(R.string.title_about_us, R.drawable.ic_about_us, (v) -> startActivity(new Intent(CenterActivity.this, AboutActivity.class))));
        return list;
    }


}
