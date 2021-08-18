package com.yyong.mirror.personal;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.yyong.middleware.ui.base.TitleActivity;
import com.yyong.mirror.BR;
import com.yyong.mirror.R;
import com.yyong.mirror.databinding.ActivityFeedbackBinding;
import com.yyong.mirror.personal.vo.QuestionTag;
import com.zero.support.common.widget.recycler.CellAdapter;
import com.zero.support.common.widget.recycler.ItemViewHolder;
import com.zero.support.common.widget.recycler.OnItemClickListener;
import com.zero.support.common.widget.recycler.SimpleViewBound;
import com.zero.support.common.widget.recycler.divider.GridItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends TitleActivity {

    private ActivityFeedbackBinding binding;
    private CellAdapter questionAdapter;
    private FeedbackViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = setBindingContentView(R.layout.activity_feedback);
        viewModel = attachSupportViewModel(FeedbackViewModel.class);
        binding.setViewModel(viewModel);
        setTitle(R.string.title_feedback);
        setupQuestion();
        binding.content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
    }


    private void setupQuestion() {
        questionAdapter = new CellAdapter(viewModel);
        questionAdapter.add(QuestionTag.class, new SimpleViewBound(BR.data, R.layout.view_bound_qustion_tag));
        binding.recyclerQuestion.addItemDecoration(new GridItemDecoration(this, 3, 9.33f, false));
        String[] array = getResources().getStringArray(R.array.feedback_question_type);
        List<QuestionTag> questionCategories = new ArrayList<>(array.length);
        final int[] types = new int[]{7, 13, 14, 15, 9, 10};
        for (int i = 0; i < array.length; i++) {
            questionCategories.add(new QuestionTag(types[i], array[i]));
        }
        if (viewModel.getCurrentQuestion() == null) {
            viewModel.setCurrentQuestion(questionCategories.get(0));
        }
        questionAdapter.submitList(questionCategories);
        questionAdapter.setEnableClick(true);
        questionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, ItemViewHolder holder) {
                viewModel.setCurrentQuestion(holder.getItem(QuestionTag.class));
            }
        });
        binding.recyclerQuestion.setLayoutManager(new GridLayoutManager(this, 3));
        binding.recyclerQuestion.setAdapter(questionAdapter);
    }
}
