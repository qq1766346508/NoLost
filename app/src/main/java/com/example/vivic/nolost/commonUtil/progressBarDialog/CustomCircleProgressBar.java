package com.example.vivic.nolost.commonUtil.progressBarDialog;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.vivic.nolost.R;

public class CustomCircleProgressBar extends View {

    private Paint mPaint = new Paint();
    private int DEF_WIDTH = dp2px(50);
    private int DEF_RADIUS = DEF_WIDTH / 2;
    private int DEF_INSIDE_COLOR = Color.RED;
    private int DEF_OUTSIDE_COLOR = Color.GRAY;
    private int DEF_STROKE_WIDTH = dp2px(3);
    private int DEF_TEXT_SIZE = dp2px(16);

    private RectF rectF;
    private int progress;
    private int startAngle;
    private int sweepAngle;
    private int endAngle;
    private ValueAnimator valueAnimator;
    private int width;
    private int radius;
    private int inside_color;
    private int outside_color;
    private int stroke_width;
    private int progress_text_color;
    private int progress_text_size;


    public CustomCircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        init(context, attrs, 0);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomCircleProgressBar, defStyleAttr, 0);
        width = (int) typedArray.getDimension(R.styleable.CustomCircleProgressBar_progress_width, DEF_WIDTH);
        radius = (int) typedArray.getDimension(R.styleable.CustomCircleProgressBar_radius, DEF_RADIUS);
        inside_color = typedArray.getColor(R.styleable.CustomCircleProgressBar_inside_color, DEF_INSIDE_COLOR);
        outside_color = typedArray.getColor(R.styleable.CustomCircleProgressBar_outside_color, DEF_OUTSIDE_COLOR);
        stroke_width = (int) typedArray.getDimension(R.styleable.CustomCircleProgressBar_stroke_width, DEF_STROKE_WIDTH);
        progress_text_color = typedArray.getColor(R.styleable.CustomCircleProgressBar_progress_text_color, DEF_INSIDE_COLOR);
        progress_text_size = (int) typedArray.getDimension(R.styleable.CustomCircleProgressBar_progress_text_size, DEF_TEXT_SIZE);
        typedArray.recycle();
        rectF = new RectF(width / 2 - radius + stroke_width, width / 2 - radius + stroke_width, width - stroke_width, width - stroke_width);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 0;
        int sizeW = MeasureSpec.getSize(widthMeasureSpec);
        int modeW = MeasureSpec.getMode(widthMeasureSpec);

        if (modeW == MeasureSpec.EXACTLY) {
            width = sizeW;
        } else {
            width = this.width;
        }
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(outside_color);
        mPaint.setStrokeWidth(stroke_width);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        canvas.drawCircle(width / 2, width / 2, radius - stroke_width, mPaint);

        mPaint.setColor(inside_color);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(rectF, startAngle, endAngle, false, mPaint);
        canvas.drawArc(rectF, endAngle, sweepAngle - endAngle, false, mPaint);
        if (sweepAngle == 360) {
            endAngle = 0;
        } else {
            endAngle = sweepAngle;
        }
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(progress_text_size);
        mPaint.setStrokeWidth(0);
        mPaint.setColor(progress_text_color);

        float baseLineY = Math.abs(mPaint.ascent() + mPaint.descent()) / 2;
        canvas.drawText(String.valueOf(progress) + "%", width / 2, width / 2 + baseLineY, mPaint);
    }


    public void setProgress(int progress) {
        this.sweepAngle = (int) (((float) progress / 100) * 360);

        valueAnimator = ValueAnimator.ofInt(endAngle, sweepAngle);
        valueAnimator.addUpdateListener(animation -> {
            CustomCircleProgressBar.this.sweepAngle = (int) animation.getAnimatedValue();
            CustomCircleProgressBar.this.progress = (int) ((int) (animation.getAnimatedValue()) * 1.0 / 360 * 100);
            postInvalidate();
        });
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(500);
        valueAnimator.start();
    }


    private int dp2px(float dp) {
        float density = getContext().getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

}

