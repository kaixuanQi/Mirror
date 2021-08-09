package com.excean.mirror.personal;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.excean.middleware.ui.base.TitleActivity;
import com.excean.mirror.BR;
import com.excean.mirror.R;
import com.excean.mirror.databinding.ActivityQuestionBinding;
import com.excean.mirror.personal.vo.Question;
import com.zero.support.common.component.RequestViewModel;
import com.zero.support.common.widget.recycler.CellAdapter;
import com.zero.support.common.widget.recycler.SimpleViewBound;

import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends TitleActivity {
    ActivityQuestionBinding binding;

    CellAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_question);
        binding = setBindingContentView(R.layout.activity_question);
        adapter = new CellAdapter(peekViewModel(RequestViewModel.class));
        adapter.setEnableClick(true);
        adapter.add(Question.class, new SimpleViewBound(BR.data, BR.holder, R.layout.view_bound_qustion_item));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.submitList(buildList());
    }

    private List<Question> buildList() {
        List<Question> list = new ArrayList<>();
        list.add(new Question(R.string.question_title_1, R.string.question_content_1));
        list.add(new Question(R.string.question_title_2, R.string.question_content_2));
        list.add(new Question(R.string.question_title_3, R.string.question_content_3));
        list.add(new Question(R.string.question_title_4, R.string.question_content_4));
        return list;
    }


}
