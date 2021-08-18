package com.yyong.mirror.personal;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableField;

import com.yyong.middleware.api.Api;
import com.yyong.middleware.ui.base.SingleDialogModel;
import com.yyong.mirror.R;
import com.yyong.mirror.api.ApiService;
import com.yyong.mirror.api.Feedback;
import com.yyong.mirror.personal.vo.QuestionTag;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.component.DataViewModel;
import com.zero.support.common.component.DialogClickEvent;
import com.zero.support.common.vo.Resource;
import com.zero.support.work.Observer;
import com.zero.support.work.Response;

public class FeedbackViewModel extends DataViewModel<Feedback, String> {
    private ObservableField<String> description = new ObservableField<>();
    private ObservableField<String> contact = new ObservableField<>();
    private QuestionTag questionTag;


    public ObservableField<String> getDescription() {
        return description;
    }


    public ObservableField<String> getContact() {
        return contact;
    }

    public void submit(View view) {
        if (TextUtils.isEmpty(contact.get())) {
            AppGlobal.sendMessage("请留下联系方式哟~");
            return;
        }
        if (TextUtils.isEmpty(description.get())) {
            AppGlobal.sendMessage("问题描述不能为空哟~");
            return;
        }
        Feedback feedback = new Feedback();
        feedback.type = questionTag.getType();
        feedback.contact = contact.get();
        feedback.content = description.get();
        notifyDataSetChanged(feedback);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
    }


    public QuestionTag getCurrentQuestion() {
        return questionTag;
    }

    public void setCurrentQuestion(QuestionTag questionTag) {
        if (this.questionTag != questionTag) {
            if (this.questionTag != null) {
                this.questionTag.getChecked().set(false);
            }
            this.questionTag = questionTag;
            questionTag.getChecked().set(true);
        }
    }


    @Override
    protected void onResourceChanged(Resource<String> resource) {
        super.onResourceChanged(resource);
        if (resource.isLoading()) {
            postLoading("反馈中");
        } else {
            Log.e("feedback", "onResourceChanged: " + resource);
            postDismiss();
        }
        if (resource.isSuccess()) {
            requestDialog(new SingleDialogModel.Builder()
                    .title(R.string.feedback_submit_dialog_title)
                    .content(R.string.feedback_submit_dialog_content)
                    .build()).click().observe(new Observer<DialogClickEvent>() {
                @Override
                public void onChanged(DialogClickEvent dialogClickEvent) {
                    dialogClickEvent.dismiss();
                    requireActivity().onBackPressed();
                }
            });
        }
    }


    @Override
    protected Response<String> performExecute(Feedback feedback) {
        Response<String> response = Api.getService(ApiService.class).getFeedback(feedback).getFuture().getValue();
        Log.e("feedback", "performExecute: " + response);
        return response;
    }
}
