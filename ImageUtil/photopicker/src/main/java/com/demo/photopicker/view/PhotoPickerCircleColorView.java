package com.demo.photopicker.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.demo.photopicker.R;

/**
 * Created by bjhl on 2018/6/4.
 */

public class PhotoPickerCircleColorView extends View {

    private int DEFAULT_FILL_COLOR = 0;

    // setup initial color
    private int mPaintColor = DEFAULT_FILL_COLOR;
    // defines paint and canvas
    private Paint mDrawPaint;

    private int mColorInterval = Color.WHITE; // 选中状态的间隔色
    private Paint mPaintInterval;

    private boolean mIsSelected = false;

    public PhotoPickerCircleColorView(Context context) {
        super(context);

        init();
    }

    public PhotoPickerCircleColorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoPickerCircleColorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PhotoPickerCircleColorView, defStyle, 0);
        mPaintColor = a.getColor(R.styleable.PhotoPickerCircleColorView_cicleColor_fillColor, DEFAULT_FILL_COLOR);

        mColorInterval = a.getColor(R.styleable.PhotoPickerCircleColorView_cicleColor_intervalColor, mColorInterval);
        a.recycle();

        init();
    }

    public void setSelected(boolean selected) {
        this.mIsSelected = selected;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();

        mPaintInterval = new Paint();
        mPaintInterval.setColor(mColorInterval);
        mPaintInterval.setAntiAlias(true);
        mPaintInterval.setStyle(Paint.Style.FILL);
    }

    // Setup paint with color and stroke styles
    private void setupPaint() {
        mDrawPaint = new Paint();
        mDrawPaint.setColor(mPaintColor);
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, getWidth() / 2.0f, mDrawPaint);

        if (mIsSelected) {
            canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, (getWidth() - 6) / 2.0f, mPaintInterval);
            canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, (getWidth() - 20) / 2.0f, mDrawPaint);
        }
    }

    public void setColor(int color) {
        mPaintColor = color;
        setupPaint();
        invalidate();
    }
}
