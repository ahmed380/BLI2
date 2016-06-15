package com.smehsn.compassinsurance.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author Sam
 */
public class LockableViewPager extends ViewPager {

    private boolean isLocked = false;

    public LockableViewPager(Context context) {
        super(context);
    }

    public LockableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !this.isLocked && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return !this.isLocked && super.onInterceptTouchEvent(event);
    }
    public void setLocked(boolean locked){
        this.isLocked = locked;
    }




}
