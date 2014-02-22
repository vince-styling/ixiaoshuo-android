package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.utils.AppLog;
import com.vincestyling.ixiaoshuo.view.PageIndicator;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class TopTabIndicator extends LinearLayout implements PageIndicator {
	private static final int INVALID_POINTER = -1;

	private final Paint mPaint = new Paint(ANTI_ALIAS_FLAG);
	private ViewPager mViewPager;
	private ViewPager.OnPageChangeListener mListener;
	private int mCurrentPage;
	private float mPageOffset;
	private int mScrollState;
	private int mBtnWidth;
	private int mBtnSpacing;
	private int mContentAreaPadding;
	private int mContentAreaMarginExtra;
	private int mTextColorOn;
	private int mTextColorOff;

	private int mTouchSlop;
	private float mLastMotionX = -1;
	private int mActivePointerId = INVALID_POINTER;
	private boolean mIsDragging;

	public TopTabIndicator(Context context) {
		this(context, null);
	}

	public TopTabIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode()) return;

		// must specifing BtnWidth or BtnSpacingWidth alternative
		// if both specified, we'll calculate all tabs size and make tabs center.
		// if only specify BtnWidth, we'll use maxmuim width and averagely the extra spacing between Button.
		// if only specify BtnSpacingWidth, we'll use maxmuim width and averagely the remaining width to each Button.
		mBtnWidth = getResources().getDimensionPixelSize(R.dimen.top_tab_btn_width);
		mBtnSpacing = getResources().getDimensionPixelSize(R.dimen.top_tab_btn_spacing);

		mContentAreaPadding = getResources().getDimensionPixelSize(R.dimen.top_tab_content_area_padding);
		mContentAreaMarginExtra = getResources().getDimensionPixelSize(R.dimen.top_tab_content_area_margin_extra);
		mTextColorOn = getResources().getColor(R.color.top_tab_text_on);
		mTextColorOff = getResources().getColor(R.color.top_tab_text_off);

		mPaint.setTextSize(getResources().getDimension(R.dimen.top_tab_btn_textsize));

		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mViewPager == null) return;

		final int count = mViewPager.getAdapter().getCount();
		if (count == 0) return;

		if (mCurrentPage >= count) {
			setCurrentItem(count - 1);
			return;
		}

		if (mBtnWidth == 0 && mBtnSpacing == 0) return;

		Drawable drawable = getResources().getDrawable(R.drawable.title_bg_content_area);

		Rect areaRect = new Rect();
		areaRect.left = getPaddingLeft();
		areaRect.right = getWidth() - getPaddingRight();
		areaRect.top = (getHeight() - mContentAreaMarginExtra - drawable.getIntrinsicHeight()) / 2;
		areaRect.bottom = areaRect.top + drawable.getIntrinsicHeight();

		drawable.setBounds(areaRect);
		drawable.draw(canvas);

		areaRect.left += mContentAreaPadding;
		areaRect.right -= mContentAreaPadding;
		areaRect.top += mContentAreaPadding;
		areaRect.bottom -= mContentAreaPadding;


		if (mBtnWidth > 0 && mBtnSpacing > 0) {
			int extraWidth = areaRect.width() - mBtnWidth * count - mBtnSpacing * (count - 1);
			if (extraWidth > 1) areaRect.left += extraWidth / 2;
		}
		else if (mBtnSpacing > 0) {
			int remainingWidth = areaRect.width() - mBtnSpacing * (count - 1);
			if (remainingWidth > count) mBtnWidth = remainingWidth / count;
		}
		else if (mBtnWidth > 0) {
			int remainingWidth = areaRect.width() - mBtnWidth * count;
			if (count > 1) {
				if (remainingWidth > count) mBtnSpacing = remainingWidth / (count - 1);
			} else {
				areaRect.left += remainingWidth / 2;
			}
		}


		Rect tabRect = new Rect(areaRect);
		tabRect.left = tabRect.left + mCurrentPage * mBtnWidth + mCurrentPage * mBtnSpacing;
		tabRect.left += mPageOffset * (mBtnWidth + mBtnSpacing);
		tabRect.right = tabRect.left + mBtnWidth;

		drawable = getResources().getDrawable(R.drawable.title_item_selected);
		drawable.setBounds(tabRect);
		drawable.draw(canvas);


		for (int pos = 0; pos < count; pos++) {
			tabRect = new Rect(areaRect);
			tabRect.left = tabRect.left + pos * mBtnWidth + pos * mBtnSpacing;
			tabRect.right = tabRect.left + mBtnWidth;

			String pageTitle = (String) mViewPager.getAdapter().getPageTitle(pos);

			RectF bounds = new RectF(tabRect);
			bounds.right = mPaint.measureText(pageTitle, 0, pageTitle.length());
			bounds.bottom = mPaint.descent() - mPaint.ascent();

			bounds.left += (tabRect.width() - bounds.right) / 2.0f;
			bounds.top += (tabRect.height() - bounds.bottom) / 2.0f;

			mPaint.setColor(pos == mCurrentPage ? mTextColorOn : mTextColorOff);

			canvas.drawText(pageTitle, bounds.left, bounds.top - mPaint.ascent(), mPaint);
		}
	}

	public boolean onTouchEvent(MotionEvent ev) {
		if (super.onTouchEvent(ev)) return true;

		if (mViewPager == null || mViewPager.getAdapter().getCount() == 0) {
			return false;
		}

		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
				mLastMotionX = ev.getX();
				break;

			case MotionEvent.ACTION_MOVE:
				final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
				final float x = MotionEventCompat.getX(ev, activePointerIndex);
				final float deltaX = x - mLastMotionX;

				if (!mIsDragging) {
					if (Math.abs(deltaX) > mTouchSlop) {
						mIsDragging = true;
					}
				}

				if (mIsDragging) {
					mLastMotionX = x;
					if (mViewPager.isFakeDragging() || mViewPager.beginFakeDrag()) {
						mViewPager.fakeDragBy(deltaX);
					}
				}

				break;

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				if (!mIsDragging) {
					final int count = mViewPager.getAdapter().getCount();
					final int width = getWidth();
					final float halfWidth = width / 2f;
					final float sixthWidth = width / 6f;

					if ((mCurrentPage > 0) && (ev.getX() < halfWidth - sixthWidth)) {
						if (action != MotionEvent.ACTION_CANCEL) {
							mViewPager.setCurrentItem(mCurrentPage - 1);
						}
						return true;
					} else if ((mCurrentPage < count - 1) && (ev.getX() > halfWidth + sixthWidth)) {
						if (action != MotionEvent.ACTION_CANCEL) {
							mViewPager.setCurrentItem(mCurrentPage + 1);
						}
						return true;
					}
					// 理解ViewPager FakeDrag 机制。
				}

				mIsDragging = false;
				mActivePointerId = INVALID_POINTER;
				if (mViewPager.isFakeDragging()) mViewPager.endFakeDrag();
				break;
		}
		return true;
	}

	@Override
	public void setViewPager(ViewPager view) {
		if (mViewPager == view) {
			return;
		}
		if (mViewPager != null) {
			mViewPager.setOnPageChangeListener(null);
		}
		if (view.getAdapter() == null) {
			throw new IllegalStateException("ViewPager does not have adapter instance.");
		}
		mViewPager = view;
		mViewPager.setOnPageChangeListener(this);
		invalidate();
	}

	@Override
	public void setViewPager(ViewPager view, int initialPosition) {
		setViewPager(view);
		setCurrentItem(initialPosition);
	}

	@Override
	public void setCurrentItem(int item) {
		if (mViewPager == null) {
			throw new IllegalStateException("ViewPager has not been bound.");
		}
		mViewPager.setCurrentItem(item);
		mCurrentPage = item;
		invalidate();
	}

	@Override
	public void notifyDataSetChanged() {
		invalidate();
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		mScrollState = state;

		if (mListener != null) {
			mListener.onPageScrollStateChanged(state);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		mCurrentPage = position;
		mPageOffset = positionOffset;
		invalidate();

		if (mListener != null) {
			mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
		}
	}

	@Override
	public void onPageSelected(int position) {
		if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
			mCurrentPage = position;
			invalidate();
		}

		if (mListener != null) {
			mListener.onPageSelected(position);
		}
	}

	@Override
	public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
		mListener = listener;
	}

}
