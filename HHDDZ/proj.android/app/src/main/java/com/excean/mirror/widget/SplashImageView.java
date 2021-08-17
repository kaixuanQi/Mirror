package com.excean.mirror.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class SplashImageView extends AppCompatImageView {
    private float density;
    private int color1 = Color.parseColor("#00fff6");
    private float radius1 = 4;

    private int color2 = Color.parseColor("#ff8dcb");
    private float radius2 = 5;
    private int color3 = Color.parseColor("#FFC600");
    private float radius3 = 4;
    private int color4 = Color.parseColor("#FF6262");
    private float radius4 = 3;
    private Paint paint;
    private int max1;
    private int max2;
    private int max3;
    private int max4;
    private int count;
    int length = 54;
    private int baseRotate = -90;

    public SplashImageView(@NonNull Context context) {
        this(context, null);
    }

    public SplashImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private final ValueAnimator animator = ValueAnimator.ofInt(0, 300);
    private int rotate = -90;


    private void init(Context context) {
        density = context.getResources().getDisplayMetrics().density;
        radius1 *= density;
        radius2 *= density;
        radius3 *= density;
        radius4 *= density;
        length *= density;
        paint = new Paint();
        paint.setAntiAlias(true);

        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setDuration(4000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new SplashImageView.SimpleListener());
        ripple = true;

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        max1 = (int) Math.round(Math.sqrt((Math.pow(getWidth() / 2f, 2) + Math.pow(getHeight() / 2f, 2)))) / 2;
        max2 = max1;
        max3 = max1;
        max4 = max1;
    }

    //周期
    //扩散-旋转-收缩
    private class SimpleListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int progress = (int) animation.getAnimatedValue();

            if (progress < 100) {

                if (SplashImageView.this.progress != progress) {
                    SplashImageView.this.progress = progress;
                    invalidate();
                }

            } else if (progress <= 200) {
                progress = progress - 100;
                if (SplashImageView.this.rotate != progress) {
                    SplashImageView.this.rotate = progress;
                    invalidate();
                }
            } else {
                progress = 300 - progress;
                if (SplashImageView.this.progress != progress) {
                    SplashImageView.this.progress = progress;
                    invalidate();
                }
            }

        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        float rotate = Math.round((SplashImageView.this.rotate) * 0.9f);
        if (rotate == 90) {
            baseRotate += 90;
        }
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;

        canvas.translate(cx, cy);
        canvas.rotate(rotate);
        paint.setColor(color1);
        canvas.drawCircle(0, -max1 * progress / 100f - length, radius1, paint);

        canvas.rotate(90);
        paint.setColor(color2);
        canvas.drawCircle(0, -max2 * progress / 100f - length, radius2, paint);

        canvas.rotate(90);
        paint.setColor(color3);
        canvas.drawCircle(0, -max3 * progress / 100f - length, radius3, paint);

        canvas.rotate(90);
        paint.setColor(color4);
        canvas.drawCircle(0, -max4 * progress / 100f - length, radius4, paint);
        canvas.rotate(-270);
        canvas.rotate(-rotate);
        canvas.translate(-cx, -cy);
        super.onDraw(canvas);

    }

    private int progress = 0;

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
}
