/*
 * Copyright (c) 2013 neevek <i@neevek.net>
 * https://github.com/neevek/Easy-PullToRefresh-Android
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;

public class PullToLoadPageListView extends ListView implements ScrollerImpl.OnFinishListener {
    private final static int DEFAULT_MAX_OVER_SCROLL_DURATION = 350;

    // boucing for a normal touch scroll gesture(happens right after the finger leaves the screen)
    private ScrollerImpl mScroller;

    private float mLastY;
    private boolean mIsTouching;
    private boolean mIsBeingTouchScrolled;
    private int mPullDistanceThreshold;
    private float mScreenDensity;

    // a threshold to tell whether the user is touch-scrolling
    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    public PullToLoadPageListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mScreenDensity = context.getResources().getDisplayMetrics().density;
        mPullDistanceThreshold = (int) (mScreenDensity * 45);

        mScroller = new ScrollerImpl(context, new DecelerateInterpolator(1.3f));

        // disable the glow effect at the edges when overscrolling.
        setOverScrollMode(OVER_SCROLL_NEVER);

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());

        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    // the top-level layout of the header view
    private PullToLoadPage mOrigHeaderView;

    // the layout, of which we will do adjust the height, and on which
    // we call requestLayout() to cause the view hierarchy to be redrawn
    private View mHeaderView;
    // for convenient adjustment of the header view height
    private ViewGroup.LayoutParams mHeaderViewLayoutParams;
    // the original height of the header view
    private int mHeaderViewHeight;

    private boolean mIsLoadingPrevPage;

    private OnLoadingPageListener mOnLoadingPageListener;

    public void setPullToLoadPrevPageView(PullToLoadPage loadingView) {
        if (mOrigHeaderView == null) {
            mHeaderView = loadingView.getRootView().getChildAt(0);
            addHeaderView(loadingView.getRootView());
            mOrigHeaderView = loadingView;
        }
    }

    private PullToLoadPage mFooterView;
    private boolean mIsLoadingNextPage;

    public void setPullToLoadNextPageView(PullToLoadPage loadingView) {
        addFooterView(loadingView.getRootView());
        mFooterView = loadingView;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (mHeaderViewHeight == 0 && mHeaderView != null) {
            mHeaderViewLayoutParams = mHeaderView.getLayoutParams();
            // after the first "laying-out", we get the original height of header view
            mHeaderViewHeight = mHeaderViewLayoutParams.height;

            // set the header height to 0 in advance. "post(Runnable)" below is queued up
            // to run in the main thread, which may delay for some time
            mHeaderViewLayoutParams.height = 0;
            // hide the header view
            post(new Runnable() {
                @Override
                public void run() {
                    setHeaderViewHeightInternal(0);
                }
            });
        }
    }

    public void triggerLoadPrevPage() {
        if (!mIsLoadingPrevPage && mOnLoadingPageListener.onLoadPrevPage()) {
            mOrigHeaderView.onStartLoading();
            mIsLoadingPrevPage = true;
        }
    }

    public void finishLoadPrevPage() {
        if (mIsLoadingPrevPage) {
            mIsLoadingPrevPage = false;

            // hide the header view with a smooth bouncing effect
            if (getFirstVisiblePosition() == 0) {
                mScroller.forceFinished(true);
                mScroller.setOnFinishListener(this);
                springBack(-mHeaderViewHeight + getScrollY());
            } else { // hide the header view without animation
                setHeaderViewHeight(0);
                onScrollFinish();
            }
        }
    }

    @Override
    public void onScrollFinish() { // on header scroll finish
        mScroller.setOnFinishListener(null);
        mOrigHeaderView.onEndLoading();

        // Remove pull-to-loading-prev-page header view after haven't previous page.
        if (!mOnLoadingPageListener.hasPrevPage()) {
            removeHeaderView(mOrigHeaderView.getRootView());
            mHeaderViewLayoutParams = null;
            mOrigHeaderView = null;
            mHeaderViewHeight = 0;
            mHeaderView = null;
        }
    }

    public void triggerLoadNextPage() {
        if (mFooterView != null && !mIsLoadingNextPage && mOnLoadingPageListener.onLoadNextPage()) {
            mFooterView.onStartLoading();
            mIsLoadingNextPage = true;
        }
    }

    public void finishLoadNextPage() {
        if (mIsLoadingNextPage) {
            mIsLoadingNextPage = false;
            mFooterView.onEndLoading();
            if (!mOnLoadingPageListener.hasNextPage()) {
                removeFooterView(mFooterView.getRootView());
                mFooterView = null;
            }
        }
    }

    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if (!isTouchEvent && mScroller.isFinished() && mVelocityTracker != null) {
            mVelocityTracker.computeCurrentVelocity((int) (16 * mScreenDensity), mMaximumVelocity);
            int yVelocity = (int) mVelocityTracker.getYVelocity(0);

            if ((Math.abs(yVelocity) > mMinimumVelocity)) {
                mScroller.fling(0, getScrollY(), 0, -yVelocity, 0, 0, -mMaximumVelocity, mMaximumVelocity);
                postInvalidate();
            }
        }
        return true;
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // for whatever reason, stop the scroller when the user *might*
                // start new touch-scroll gestures.
                mScroller.forceFinished(true);

                mLastY = ev.getRawY();
                mIsTouching = true;

                initOrResetVelocityTracker();
                mVelocityTracker.addMovement(ev);

                mMotionPosition = findMotionRow(ev.getY());
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private VelocityTracker mVelocityTracker;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mVelocityTracker.addMovement(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float y = ev.getRawY();
                int deltaY = (int) (y - mLastY);

                if (deltaY == 0) {
                    return true;
                }

                if (mIsBeingTouchScrolled) {
                    if (getChildCount() > 0) {
                        handleTouchScroll(deltaY);
                    }

                    mLastY = y;
                } else if (Math.abs(deltaY) > mTouchSlop) {
                    // check if the delta-y has exceeded the threshold
                    mIsBeingTouchScrolled = true;
                    mLastY = y;
                    break;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsTouching = false;
                mIsBeingTouchScrolled = false;

                // 'getScrollY != 0' means that content of the ListView is off screen.
                // Or if it is not in "loading prev page" state while height of the header view
                // is greater than 0, we must set it to 0 with a smooth bounce effect
                if ((getScrollY() != 0 || (!mIsLoadingPrevPage && getCurrentHeaderViewHeight() > 0))) {
                    springBack();

                    // it is safe to digest the touch events here
                    return true;
                }

                break;
        }

        int curScrollY = getScrollY();

        // if not in 'loading prev page' state or scrollY is less than zero, and height of
        // header view is greater than zero. we should keep the the first item of the
        // ListView always at the top(we are decreasing height of the header view, without
        // calling setSelection(0), we will decrease height of the header view and scroll
        // the ListView itself at the same time, which will cause scrolling too fast
        // when decreasing height of the header view)
        if ((!mIsLoadingPrevPage && getCurrentHeaderViewHeight() > 0) || curScrollY < 0) {
//            setSelection(0);
            return true;
        } else if (curScrollY > 0) {
//            setSelection(getCount() - 1);
            return true;
        }

        try {
            // let the original ListView handle the touch events
            return super.onTouchEvent(ev);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void handleTouchScroll(int deltaY) {
        boolean reachTopEdge = reachTopEdge();
        boolean reachBottomEdge = reachBottomEdge();
        if (!reachTopEdge && !reachBottomEdge) {
            // since we are at the middle of the ListView, we don't
            // need to handle any touch events
            return;
        }

        final int scrollY = getScrollY();

        int listViewHeight = getHeight();
        // 0.4f is just a number that gives OK effect out of many tests. it means nothing special
        float scale = ((float) listViewHeight - Math.abs(scrollY) - getCurrentHeaderViewHeight()) / getHeight() * 0.4f;

        int newDeltaY = Math.round(deltaY * scale);

        if (newDeltaY != 0) {
            deltaY = newDeltaY;
        }

        if (reachTopEdge) {
            if (mIsLoadingPrevPage) return;
            if (deltaY > 0) {
                scrollDown(deltaY);
            } else {
                scrollUp(deltaY);
            }
        } else {
            if (deltaY > 0) {
                if (scrollY > 0) {
                    // when scrollY is greater than 0, it means we reach the bottom of the list
                    // and the ListView is scrolled off the screen from the bottom, now we
                    // scrollDown() to scroll it back, otherwise, we just let the original ListView
                    // handle the scroll_down events
                    scrollDown(Math.min(deltaY, scrollY));
                }
            } else {
                scrollUp(deltaY);
            }
        }
    }

    private boolean reachTopEdge() {
        return getChildCount() <= 0 ||
                getFirstVisiblePosition() == 0 && getChildAt(0).getTop() == 0;
    }

    private boolean reachBottomEdge() {
        return getChildCount() <= 0 ||
                getLastVisiblePosition() == getCount() - 1 && getChildAt(getChildCount() - 1).getBottom() <= getHeight();
    }

    private void springBack() {
        int scrollY = getScrollY();

        int curHeaderViewHeight = getCurrentHeaderViewHeight();
        if (curHeaderViewHeight == mHeaderViewHeight && mHeaderViewHeight > 0) {
            // There is a bug when user scroll the y-axis just equal zero.
            if (!mIsLoadingPrevPage && scrollY < 0) {
                scrollY -= curHeaderViewHeight;
            }
        } else {
            scrollY -= curHeaderViewHeight;
        }

        if (scrollY != 0) springBack(scrollY);
    }

    private void springBack(int scrollY) {
        mScroller.startScroll(0, scrollY, 0, -scrollY, DEFAULT_MAX_OVER_SCROLL_DURATION);
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int scrollY = getScrollY();

            // if not in "loading prev page" state, we must
            // decrease height of the header view to 0
            if (!mIsLoadingPrevPage && getCurrentHeaderViewHeight() > 0) {
                scrollY -= getCurrentHeaderViewHeight();
            }

            final int deltaY = mScroller.getCurrY() - scrollY;

            if (deltaY != 0) {
                if (deltaY < 0) {
                    scrollDown(-deltaY);
                } else {
                    scrollUp(-deltaY);
                }
            }

            postInvalidate();

        } else if (!mIsTouching && (getScrollY() != 0 || (!mIsLoadingPrevPage && getCurrentHeaderViewHeight() != 0))) {
            springBack();
        }

        super.computeScroll();
    }

    /**
     * scrollDown() does 2 things:
     * <p/>
     * 1. check if height of the header view is greater than 0, if so, decrease it to 0
     * <p/>
     * 2. scroll content of the ListView off the screen any there's any deltaY left(i.e.
     * deltaY is not 0)
     */
    private void scrollDown(int deltaY) {
        if (!mIsLoadingPrevPage && getScrollY() <= 0 && reachTopEdge()) {
            final int curHeaderViewHeight = getCurrentHeaderViewHeight();
            if (curHeaderViewHeight < mHeaderViewHeight) {
                int newHeaderViewHeight = curHeaderViewHeight + deltaY;
                if (newHeaderViewHeight < mHeaderViewHeight) {
                    setHeaderViewHeight(newHeaderViewHeight);
                    return;
                } else {
                    setHeaderViewHeight(mHeaderViewHeight);
                    deltaY = newHeaderViewHeight - mHeaderViewHeight;
                }
            }
        }

        scrollBy(0, -deltaY);
    }

    /**
     * scrollUp() does 3 things:
     * <p/>
     * 1. if scrollY is less than 0, it means we have scrolled the list off the screen
     * from the top, now we scroll back and make the list to reach the top edge of
     * the screen.
     * <p/>
     * 2. check height of the header view and see if it is greater than 0, if so, we
     * decrease it and make it zero.
     * <p/>
     * 3. now check if we have scrolled the list to reach the bottom of the screen, if so
     * we scroll the list off the screen from the bottom.
     */
    private void scrollUp(int deltaY) {
        final int scrollY = getScrollY();
        if (scrollY < 0) {
            if (scrollY < deltaY) {     // both scrollY and deltaY are less than 0
                scrollBy(0, -deltaY);
                return;
            } else {
                scrollTo(0, 0);
                deltaY -= scrollY;
                if (deltaY == 0) return;
            }
        }

        if (!mIsLoadingPrevPage) {
            int curHeaderViewHeight = getCurrentHeaderViewHeight();
            if (curHeaderViewHeight > 0) {

                int newHeaderViewHeight = curHeaderViewHeight + deltaY;
                if (newHeaderViewHeight > 0) {
                    setHeaderViewHeight(newHeaderViewHeight);
                    return;
                } else {
                    setHeaderViewHeight(0);
                    deltaY = newHeaderViewHeight;
                }
            }
        }

        if (reachBottomEdge()) {
            scrollBy(0, -deltaY);
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        int oldScrollY = getScrollY();
        super.scrollTo(x, y);

        if (mHeaderView != null && y < 0 && !mIsLoadingPrevPage) {
            oldScrollY = Math.abs(oldScrollY);
            int curTotalScrollY = getCurrentHeaderViewHeight() + (-y);
            int halfPullDistanceThreshold = mPullDistanceThreshold / 2;
            if (curTotalScrollY > halfPullDistanceThreshold) {
                if (oldScrollY > halfPullDistanceThreshold) {
                    if (oldScrollY < mPullDistanceThreshold && curTotalScrollY >= mPullDistanceThreshold) {
                        triggerLoadPrevPage();
                        springBack();
                    }
                }
            }
        } else if (mFooterView != null && !mIsLoadingNextPage) {
            int halfPullDistanceThreshold = mPullDistanceThreshold / 2;
            if (y > halfPullDistanceThreshold) {
                if (oldScrollY > halfPullDistanceThreshold && oldScrollY < mPullDistanceThreshold && y >= mPullDistanceThreshold) {
                    triggerLoadNextPage();
                    springBack();
                }
            }
        }
    }

    private void setHeaderViewHeight(int height) {
        if (mHeaderViewLayoutParams != null && (mHeaderViewLayoutParams.height != 0 || height != 0)) {
            setHeaderViewHeightInternal(height);
        }
    }

    private void setHeaderViewHeightInternal(int height) {
        mHeaderViewLayoutParams.height = height;

        // if mHeaderView is visible(I mean within the confines of the visible screen), we should
        // request the mHeaderView to re-layout itself, if mHeaderView is not visible, we should
        // redraw the ListView itself, which ensures correct scroll position of the ListView.
        if (mHeaderView.isShown()) {
            mHeaderView.requestLayout();
        } else {
            invalidate();
        }
    }

    private int getCurrentHeaderViewHeight() {
        return mHeaderViewLayoutParams != null ? mHeaderViewLayoutParams.height : 0;
    }

    public void setOnLoadingPageListener(OnLoadingPageListener onLoadingPageListener) {
        mOnLoadingPageListener = onLoadingPageListener;
    }

    // see http://stackoverflow.com/a/9173866/668963
    @Override
    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        } catch (IllegalArgumentException iae) {
            // Workaround for http://code.google.com/p/android/issues/detail?id=22751
        }
    }

    // see http://stackoverflow.com/a/8433777/668963
    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            // ignore this exception
        }
    }

    public static interface OnLoadingPageListener {
        boolean onLoadPrevPage();

        boolean onLoadNextPage();

        boolean hasPrevPage();

        boolean hasNextPage();
    }

    public static interface PullToLoadCallback {
        void onStartLoading();

        void onEndLoading();
    }

    private int mMotionPosition;

    public int getMotionPosition() {
        return mMotionPosition;
    }

    /**
     * Find which position is motion on. this method copy into public from 4.0 source code.
     *
     * @param y Y coordinate of the motion event.
     * @return Selected index (starting at 0) of the data item.
     */
    public int findMotionRow(float y) {
        int childCount = getChildCount();
        if (childCount > 0) {
            if (!isStackFromBottom()) {
                for (int i = 0; i < childCount; i++) {
                    View v = getChildAt(i);
                    if (y <= v.getBottom()) {
                        return getFirstVisiblePosition() + i;
                    }
                }
            } else {
                for (int i = childCount - 1; i >= 0; i--) {
                    View v = getChildAt(i);
                    if (y >= v.getTop()) {
                        return getFirstVisiblePosition() + i;
                    }
                }
            }
        }
        return INVALID_POSITION;
    }
}
