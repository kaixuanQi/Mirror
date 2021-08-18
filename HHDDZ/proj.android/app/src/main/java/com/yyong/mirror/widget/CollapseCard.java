package com.yyong.mirror.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.yyong.mirror.R;

public class CollapseCard extends FrameLayout {
    private boolean expanded = false;
    private TextView cardTitleView;
    private TextView cardDescriptionView;
    private ImageView expandIcon;
    private View titleContainer;
    private int height;


    public CollapseCard(@NonNull Context context) {
        super(context, null);
    }

    public CollapseCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapseCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        View root = LayoutInflater.from(getContext())
                .inflate(R.layout.collapsible_card_content, this, true);

        titleContainer = root.findViewById(R.id.title_container);
        cardTitleView = root.findViewById(R.id.card_title);

        cardDescriptionView = root.findViewById(R.id.card_description);
        expandIcon = root.findViewById(R.id.expand_icon);

        titleContainer.setOnClickListener((v) -> {
            toggleExpanded();
        });
        titleContainer.setBackgroundColor(Color.parseColor("#f8f8f8"));
    }

    private void toggleExpanded() {
        expanded = !expanded;
        if (expanded) {

            cardDescriptionView.setVisibility(VISIBLE);
            ObjectAnimator.ofFloat(expandIcon, "rotation", 0, 180).setDuration(100).start();
            ObjectAnimator animator = ObjectAnimator.ofInt(cardDescriptionView, "height", 0, height).setDuration(100);
            animator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    cardDescriptionView.setVisibility(VISIBLE);
                    titleContainer.setBackgroundColor(Color.parseColor("#ffffff"));
                }
            });
            animator.start();

        } else {

            ObjectAnimator.ofFloat(expandIcon, "rotation", 180, 0).setDuration(100).start();

            ObjectAnimator animator = ObjectAnimator.ofInt(cardDescriptionView, "height", height, 0)
                    .setDuration(100);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    cardDescriptionView.setVisibility(View.GONE);
                    titleContainer.setBackgroundColor(Color.parseColor("#f8f8f8"));
                }
            });
            animator.start();

        }
    }

    public void setCollapseCardContent(int content) {
        cardDescriptionView.setText(content);
        if (!expanded) {
            cardDescriptionView.setVisibility(VISIBLE);
        }
        cardDescriptionView.post(new Runnable() {
            @Override
            public void run() {
                height = cardDescriptionView.getHeight();
                if (!expanded) {
                    cardDescriptionView.setVisibility(GONE);
                }
            }
        });
    }


    @BindingAdapter("collapseCardTitle")
    public static void setCollapseCardTitle(CollapseCard collapseCard, int title) {
        collapseCard.cardTitleView.setText(title);
    }

    @BindingAdapter("collapseCardContent")
    public static void setCollapseCardContent(CollapseCard collapseCard, int content) {
        collapseCard.setCollapseCardContent(content);
    }

    @BindingAdapter("collapseCardExpanded")
    public static void setCollapseCardContent(CollapseCard collapseCard, boolean expanded) {
        if (expanded != collapseCard.expanded) {
            collapseCard.toggleExpanded();
        }
    }

}
