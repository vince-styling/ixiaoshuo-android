package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.view.PageIndicator;

public class FinderTabIndicator extends LinearLayout implements PageIndicator {
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;

    private int mBtnWidth;
    private int mBtnHeight;

    private int[][] menus = {
            {R.drawable.finder_new_on, R.drawable.finder_new_off},
            {R.drawable.finder_hot_on, R.drawable.finder_hot_off},
            {R.drawable.finder_finished_on, R.drawable.finder_finished_off},
            {R.drawable.finder_categories_on, R.drawable.finder_categories_off},
    };

    public FinderTabIndicator(Context context) {
        this(context, null);
    }

    public FinderTabIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        Bitmap measureItemBimp = BitmapFactory.decodeResource(getResources(), menus[0][0]);
        mBtnWidth = measureItemBimp.getWidth();
        mBtnHeight = measureItemBimp.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mViewPager == null) return;

        final int count = mViewPager.getAdapter().getCount();
        if (count == 0) return;

        Rect menuRect = new Rect();
        menuRect.left = getPaddingLeft();
        menuRect.right = getWidth() - getPaddingRight();
        menuRect.top = (getHeight() - mBtnHeight) / 2;
        menuRect.bottom = menuRect.top + mBtnHeight;

        Drawable menuDrawable;
        int btnSpacing = (menuRect.width() - mBtnWidth * count) / (count - 1);

        for (int pos = 0; pos < count; pos++) {
            Rect tabRect = new Rect(menuRect);
            tabRect.left += pos * (mBtnWidth + btnSpacing);
            tabRect.right = tabRect.left + mBtnWidth;

            menuDrawable = getResources().getDrawable(menus[pos][pos == mViewPager.getCurrentItem() ? 0 : 1]);
            menuDrawable.setBounds(tabRect);
            menuDrawable.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mViewPager == null) return false;

        final int count = mViewPager.getAdapter().getCount();
        if (count == 0) return false;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:

                float btnWidth = getWidth() / count;
                RectF tabRect = new RectF();
                tabRect.bottom = getHeight();

                for (int pos = 0; pos < count; pos++) {
                    tabRect.left = pos * btnWidth;
                    tabRect.right = tabRect.left + btnWidth;

                    if (tabRect.contains(ev.getX(), ev.getY())) {
                        setCurrentItem(pos);
                        return true;
                    }
                }
                break;
        }

        return true;
    }

    @Override
    public void setViewPager(ViewPager view) {
        if (mViewPager == view) return;

        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
        }

        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        mViewPager = view;
        mViewPager.setOnPageChangeListener(this);
        if (mListener != null) mListener.onPageSelected(0);

        invalidate();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
        if (mListener != null) mListener.onPageSelected(initialPosition);
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mViewPager.setCurrentItem(item);
        invalidate();
    }

    @Override
    public void notifyDataSetChanged() {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mListener != null) mListener.onPageScrollStateChanged(state);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mListener != null)
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        if (mListener != null) mListener.onPageSelected(position);
        invalidate();
    }

}
