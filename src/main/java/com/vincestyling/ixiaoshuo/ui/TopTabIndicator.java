package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.view.PageIndicator;

public class TopTabIndicator extends LinearLayout implements PageIndicator {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Drawable mAreaDrawable;

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;

    private int mScrollingToPage;
    private float mPageOffset;

    private int mBtnWidth;
    private int mBtnSpacing;
    private int mContentAreaPadding;
    private int mContentAreaMarginExtra;
    private int mTextColorOn;
    private int mTextColorOff;

    public TopTabIndicator(Context context) {
        this(context, null);
    }

    public TopTabIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) return;

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TopTabIndicator);

        // must specifing BtnWidth or BtnSpacingWidth alternative
        // if both specified, we'll calculate all tabs size and make tabs center.
        // if only specify BtnWidth, we'll use maxmuim width and averagely the extra spacing between Button.
        // if only specify BtnSpacingWidth, we'll use maxmuim width and averagely the remaining width to each Button.
        mBtnWidth = array.getDimensionPixelOffset(R.styleable.TopTabIndicator_btnWidth, 0);
        mBtnSpacing = array.getDimensionPixelOffset(R.styleable.TopTabIndicator_btnSpacing, 0);

        array.recycle();

        mContentAreaPadding = getResources().getDimensionPixelSize(R.dimen.top_tab_content_area_padding);
        mContentAreaMarginExtra = getResources().getDimensionPixelSize(R.dimen.top_tab_content_area_margin_extra);
        mTextColorOn = getResources().getColor(R.color.top_tab_text_on);
        mTextColorOff = getResources().getColor(R.color.top_tab_text_off);

        mPaint.setTextSize(getResources().getDimension(R.dimen.top_tab_btn_textsize));

        mAreaDrawable = getResources().getDrawable(R.drawable.title_bg_content_area);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mViewPager == null) return;

        final int count = mViewPager.getAdapter().getCount();
        if (count == 0) return;

        if (mBtnWidth == 0 && mBtnSpacing == 0) return;

        Rect areaRect = new Rect();
        areaRect.left = getPaddingLeft();
        areaRect.right = getWidth() - getPaddingRight();
        areaRect.top = (getHeight() - mContentAreaMarginExtra - mAreaDrawable.getIntrinsicHeight()) / 2;
        areaRect.bottom = areaRect.top + mAreaDrawable.getIntrinsicHeight();

        mAreaDrawable.setBounds(areaRect);
        mAreaDrawable.draw(canvas);

        areaRect.left += mContentAreaPadding;
        areaRect.right -= mContentAreaPadding;
        areaRect.top += mContentAreaPadding;
        areaRect.bottom -= mContentAreaPadding;


        if (mBtnWidth > 0 && mBtnSpacing > 0) {
            int extraWidth = areaRect.width() - mBtnWidth * count - mBtnSpacing * (count - 1);
            if (extraWidth > 1) areaRect.left += extraWidth / 2;
        } else if (mBtnSpacing > 0) {
            int remainingWidth = areaRect.width() - mBtnSpacing * (count - 1);
            if (remainingWidth > count) mBtnWidth = remainingWidth / count;
        } else if (mBtnWidth > 0) {
            int remainingWidth = areaRect.width() - mBtnWidth * count;
            if (count > 1) {
                if (remainingWidth > count) mBtnSpacing = remainingWidth / (count - 1);
            } else {
                areaRect.left += remainingWidth / 2;
            }
        }


        Rect tabRect = new Rect(areaRect);
        tabRect.left += (mScrollingToPage + mPageOffset) * (mBtnWidth + mBtnSpacing);
        tabRect.right = tabRect.left + mBtnWidth;

        Drawable selectedDrawle = getResources().getDrawable(R.drawable.title_item_selected);
        selectedDrawle.setBounds(tabRect);
        selectedDrawle.draw(canvas);


        for (int pos = 0; pos < count; pos++) {
            tabRect = new Rect(areaRect);
            tabRect.left += pos * (mBtnWidth + mBtnSpacing);
            tabRect.right = tabRect.left + mBtnWidth;

            String pageTitle = (String) mViewPager.getAdapter().getPageTitle(pos);

            RectF bounds = new RectF(tabRect);
            bounds.right = mPaint.measureText(pageTitle);
            bounds.bottom = mPaint.descent() - mPaint.ascent();

            bounds.left += (tabRect.width() - bounds.right) / 2.0f;
            bounds.top += (tabRect.height() - bounds.bottom) / 2.0f;

            mPaint.setColor(pos == mViewPager.getCurrentItem() ? mTextColorOn : mTextColorOff);

            canvas.drawText(pageTitle, bounds.left, bounds.top - mPaint.ascent(), mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mViewPager == null) return false;

        final int count = mViewPager.getAdapter().getCount();
        if (count == 0) return false;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:

                Rect areaRect = new Rect();
                areaRect.left = getPaddingLeft();
                areaRect.right = getWidth() - getPaddingRight();
                areaRect.top = (getHeight() - mContentAreaMarginExtra - mAreaDrawable.getIntrinsicHeight()) / 2;
                areaRect.bottom = areaRect.top + mAreaDrawable.getIntrinsicHeight();

                for (int pos = 0; pos < count; pos++) {
                    RectF tabRect = new RectF(areaRect);
                    tabRect.left += pos * (mBtnWidth + mBtnSpacing);
                    tabRect.right = tabRect.left + mBtnWidth;

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
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mListener != null) mListener.onPageScrollStateChanged(state);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mPageOffset = positionOffset;
        mScrollingToPage = position;
        invalidate();
        if (mListener != null)
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        if (mListener != null) mListener.onPageSelected(position);
    }

}
