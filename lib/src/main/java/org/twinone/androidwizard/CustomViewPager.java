package org.twinone.androidwizard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * @author Luuk W. (Twinone).
 */
public class CustomViewPager extends ViewPager {

    private boolean pagingEnabled = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPagingEnabled(boolean enabled) {
        this.pagingEnabled = enabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!pagingEnabled) return false;
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!pagingEnabled) return false;
        return super.onInterceptTouchEvent(event);
    }
}
