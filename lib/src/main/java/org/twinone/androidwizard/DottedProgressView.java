package org.twinone.androidwizard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

/**
 * @author Luuk W. (Twinone).
 */
public class DottedProgressView extends View {

    private static final String TAG = "DottedProgressView";
    private int mCount;
    private int mCurrent = 1;
    private boolean mShowDots = false;

    private Paint mCompletedPaint = new Paint();
    private Paint mLeftPaint = new Paint();

    public DottedProgressView(Context context) {
        this(context, null);
    }

    public DottedProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DottedProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        int colorAccent = typedValue.data;

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DottedProgressView,
                0, 0);

        try {
            mCompletedPaint.setColor(a.getColor(R.styleable.DottedProgressView_completedColor, colorAccent));
            mLeftPaint.setColor(a.getColor(R.styleable.DottedProgressView_leftColor, 0xFF444444));
        } finally {
            a.recycle();
        }

        mCompletedPaint.setColor(colorAccent);
        mCompletedPaint.setStrokeWidth(dpToPx(3));
        mLeftPaint.setStrokeWidth(dpToPx(3));
    }

    public void setCount(int count) {
        mCount = count;

        // Clamp current
        setCurrent(mCurrent);

        requestLayout();
    }

    public int getCount() {
        return mCount;
    }

    public void setCurrent(int current) {
        mCurrent = current;
        if (mCurrent > mCount) mCurrent = mCount;
        if (mCurrent < 0) mCurrent = 0;

        invalidate();
    }

    public int getCurrent() {
        return mCurrent;
    }

    public void next() {
        setCurrent(mCurrent + 1);
    }

    public void prev() {
        setCurrent(mCurrent - 1);
    }

    public void setShowDots(boolean showDots) {
        mShowDots = showDots;
    }

    public void setCompletedColor(int color) {
        mCompletedPaint.setColor(color);
        invalidate();
    }

    public int getCompletedColor() {
        return mCompletedPaint.getColor();
    }

    public void setLeftColor(int color) {
        mLeftPaint.setColor(color);
        invalidate();
    }

    public int getLeftColor() {
        return mLeftPaint.getColor();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mCount < 2) return;

        int w = getWidth();
        int h = getHeight();
        int r = h / 2;
        if (mShowDots) {
            int step = (int) ((float) (w - 2 * r) / (mCount - 1));

            int split = r + mCurrent * step - step / 2;
            split = Math.min(Math.max(r, split), w - r);

            canvas.drawLine(r, r, split, r, mCompletedPaint);
            canvas.drawLine(split, r, w - r, r, mLeftPaint);

            for (int i = 0; i < mCount; i++) {
                canvas.drawCircle(r + step * i, r, r, i < mCurrent ? mCompletedPaint : mLeftPaint);
            }
        } else {
            int step = (int) (w / (float) (mCount));
            int split = step * mCurrent;
            split = Math.min(Math.max(0, split), w);

            canvas.drawLine(0, r, split, r, mCompletedPaint);
            canvas.drawLine(split, r, w, r, mLeftPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredHeight = dpToPx(20);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        int desiredWidth = (int) (height * 2 * mCount);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        setMeasuredDimension(width, height);
    }

    public int dpToPx(int dp) {
        return Math.round(dp * (getContext().getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
