package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class ScrollerImpl extends Scroller {
    public ScrollerImpl(Context context) {
        super(context);
    }

    public ScrollerImpl(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public ScrollerImpl(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    private boolean mIsStarted;
    private OnFinishListener mOnFinishListener;

    @Override
    public boolean computeScrollOffset() {
        boolean result = super.computeScrollOffset();
        if (!result && mIsStarted) {
            // Don't let any exception impact the scroll animation.
            try {
                mOnFinishListener.onScrollFinish();
            } catch (Exception e) {
            }
            mIsStarted = false;
        }
        return result;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy);
        mIsStarted = true;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, duration);
        mIsStarted = true;
    }

    @Override
    public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
        super.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
        mIsStarted = true;
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        mOnFinishListener = onFinishListener;
    }

    public static interface OnFinishListener {
        void onScrollFinish();
    }
}
