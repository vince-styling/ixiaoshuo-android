package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.view.BookFinderView;
import com.duowan.mobile.ixiaoshuo.view.BookshelfView;

public abstract class SingleLineGridView extends View {
	public SingleLineGridView(Context context) {
		super(context);
		init();
	}

	public SingleLineGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	protected SparseArray<GridItem> mGridItems;

	protected int mPaddingTop;
	protected int mSelectedItemId;
	protected Drawable mHighlightDrawable;

	protected abstract void init();

	@Override
	protected void onDraw(Canvas canvas) {
		int itemWidth = getWidth() / mGridItems.size();
		int iconDstHeight = getHeight() - mPaddingTop * 2;

		for (int i = 0; i < mGridItems.size(); i++) {
			int itemId = mGridItems.keyAt(i);
			GridItem gridItem = mGridItems.get(itemId);

			if (itemId == mSelectedItemId) {
				if (mHighlightDrawable != null) {
					mHighlightDrawable.setBounds(0, 0, itemWidth, getHeight());
					mHighlightDrawable.draw(canvas);
				}
			}

			Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(),
					itemId == mSelectedItemId ? gridItem.menuOnResId : gridItem.menuOffResId);

			int iconDstWidth = (int) (iconDstHeight * ((double) iconBitmap.getWidth() / iconBitmap.getHeight()));

			Rect dstRect = new Rect();
			dstRect.left = (itemWidth - iconDstWidth) / 2;
			dstRect.top = mPaddingTop;
			dstRect.right = dstRect.left + iconDstWidth;
			dstRect.bottom = mPaddingTop + iconDstHeight;

			canvas.drawBitmap(iconBitmap, null, dstRect, null);
			canvas.translate(itemWidth, 0);
		}
		canvas.restore();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				if (event.getY() > 0) {
					int itemWidth = getWidth() / mGridItems.size();
					int itemIndex = (int) (event.getX() / itemWidth);
					int itemId = mGridItems.keyAt(itemIndex);
					if (itemId != mSelectedItemId) {
						GridItem gridItem = mGridItems.get(itemId);
						gridItem.clickEvent.onClick();
						mSelectedItemId = itemId;
						invalidate();
						playSoundEffect(SoundEffectConstants.CLICK);
					}
				}
				return true;
		}
		return true;
	}

	protected static class GridItem {
		final int menuOffResId, menuOnResId;
		final ClickEvent clickEvent;
		GridItem(int menuOnResId, int menuOffResId, ClickEvent clickEvent) {
			this.menuOffResId = menuOffResId;
			this.menuOnResId = menuOnResId;
			this.clickEvent = clickEvent;
		}
	}

	protected static interface ClickEvent {
		void onClick();
	}

	protected MainActivity getActivity() {
		return (MainActivity) getContext();
	}

}
