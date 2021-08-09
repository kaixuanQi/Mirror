package com.excean.mirror.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;

import com.excean.mirror.R;

/**
 * 新手引导带有波纹效果的动画
 */
public class RippleFab extends AppCompatImageView {
    public RippleFab(@NonNull Context context) {
        this(context, null);
    }

    public RippleFab(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleFab(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private final ValueAnimator animator = ValueAnimator.ofInt(100, 0);
    private Drawable drawable;
    private int RIPPLE = 10;

    private void init(Context context) {
        drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_guide_bg, null);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new SimpleListener());
    }


    private class SimpleListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int progress = (int) animation.getAnimatedValue();
            if (RippleFab.this.progress != progress) {
                RippleFab.this.progress = progress;
                invalidate();
            }

        }
    }

    private int progress;

    private boolean ripple;

    public void startRipple() {
        animator.cancel();
        ripple = true;
        animator.start();
    }

    public void stopRipple() {
        animator.cancel();
        ripple = false;
        progress = 0;
        postInvalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (ripple) {
            animator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (ripple) {
            animator.cancel();
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        RIPPLE = getPaddingStart();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (ripple) {
            int offset = Math.round(RIPPLE + RIPPLE * progress / 100f);
            drawable.setAlpha(80);
            drawable.setBounds(offset, offset, getWidth() - offset, getHeight() - offset);
            drawable.draw(canvas);
            drawable.setAlpha(60);
            offset = Math.round(2 * RIPPLE * progress / 100f);
            drawable.setBounds(offset, offset, getWidth() - offset, getHeight() - offset);
            drawable.draw(canvas);
        }
        super.onDraw(canvas);
    }
}
