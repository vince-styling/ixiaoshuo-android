package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.event.OnGridItemClickListener;

public abstract class GridView extends View {

	public GridView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.GridView);
		mColumnCount = typeArray.getInteger(R.styleable.GridView_columnCount, 0);
		typeArray.recycle();

		mPaddingTop = getResources().getDimensionPixelSize(R.dimen.globalMenuPadding);

		mItems = new SparseArray<GridItem>();
	}

	private SparseArray<GridItem> mItems;

	// we don't specify LineCount, because we use ColumnCount to calculate it
	private int mColumnCount;

	// at first, we don't need padding other such as paddingLeft, paddingRight, paddingBottom
	private int mPaddingTop;

	// the selected itemId, notice : only selectItem method can modify it
	private int mSelectedItemId;

	// highlight drawable, must not transparent, allows nine-patch or normal image
	protected Drawable mHighlightDrawable;

	// this ItemsBitmap draw with all OFF status grid item, just draw once then reuse
	private Bitmap mItemsBitmap;

	private int mItemWidth, mItemHeight;

	private boolean mInitialized;

	private synchronized void init() {
		mInitialized = true;
		int width = getWidth();
		int height = getHeight();

		// if not specify ColumnCount, we'll use Items size
		if (mColumnCount == 0) mColumnCount = mItems.size();
		mItemWidth = (int) Math.ceil((width) / mColumnCount);
		mItemHeight = height / (mItems.size() / mColumnCount);

		mItemsBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		mItemsBitmap.eraseColor(Color.TRANSPARENT);

		Canvas tmpCanvas = new Canvas(mItemsBitmap);
		drawItems(tmpCanvas);
	}

	private void drawItems(Canvas canvas) {
		for (int index = 0; index < mItems.size(); index++) {
			Rect itemRect = getItemRect(index);

			canvas.save(Canvas.CLIP_SAVE_FLAG);
			canvas.clipRect(itemRect);

			drawItem(canvas, itemRect, index, false);

			canvas.restore();
		}
	}

	private Rect getItemRect(int index) {
		Rect itemRect = new Rect();
		itemRect.left = (index % mColumnCount) * mItemWidth;
		itemRect.right = itemRect.left + mItemWidth;
		itemRect.top = mItemHeight * (index / mColumnCount);
		itemRect.bottom = itemRect.top + mItemHeight;
		return itemRect;
	}

	private void drawItem(Canvas canvas, Rect itemRect, int index, boolean isOn) {
		GridItem item = mItems.valueAt(index);
		Bitmap itemBitmap = isOn ? item.getOnBitmap(getResources()) : item.getOffBitmap(getResources());
		getItemBounds(itemRect, itemBitmap);
		canvas.drawBitmap(itemBitmap, null, itemRect, null);
	}

	// we don't scale Bitmap, so an appropriate dimension image will need
	private void getItemBounds(Rect itemRect, Bitmap itemBitmap) {
		itemRect.left += (itemRect.width() - itemBitmap.getWidth()) / 2;
		itemRect.right = itemRect.left + itemBitmap.getWidth();
		itemRect.top += (itemRect.height() - itemBitmap.getHeight()) / 2;
		itemRect.bottom = itemRect.top + itemBitmap.getHeight();
	}

	private void highlightItem(Canvas canvas) {
		if (mSelectedItemId < 1) return;

		int index = mItems.indexOfKey(mSelectedItemId);
		Rect itemRect = getItemRect(index);
		canvas.save(Canvas.CLIP_SAVE_FLAG);
		canvas.clipRect(itemRect);

		if (mHighlightDrawable != null) {
			mHighlightDrawable.setBounds(itemRect);
			mHighlightDrawable.draw(canvas);
		}

		drawItem(canvas, itemRect, index, true);

		canvas.restore();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (!mInitialized) init();
		canvas.drawBitmap(mItemsBitmap, 0, 0, null);
		highlightItem(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				for (int index = 0; index < mItems.size(); index++) {
					Rect itemRect = getItemRect(index);
					if (itemRect.contains((int) event.getX(), (int) event.getY())) {
						int itemId = mItems.keyAt(index);
						if (selectItem(itemId)) mItems.get(itemId).performClick();
						break;
					}
				}
				return true;
		}
		return true;
	}

	public void putItem(GridItem gridItem) {
		mItems.put(gridItem.getItemId(), gridItem);
	}

	public boolean selectItem(int itemId) {
		if (mSelectedItemId != itemId) {
			mSelectedItemId = itemId;
			invalidate();
			return true;
		}
		return false;
	}

	protected static class GridItem {
		int offResId, onResId;
		OnGridItemClickListener clickEvent;

		int getItemId() {
			return clickEvent.getGridItemId();
		}

		void performClick() {
			if (clickEvent != null) clickEvent.onGridItemClick();
		}

		public GridItem(int onResId, int offResId, OnGridItemClickListener clickEvent) {
			this.onResId = onResId;
			this.offResId = offResId;
			this.clickEvent = clickEvent;
		}

		public Bitmap getOnBitmap(Resources res) {
			return BitmapFactory.decodeResource(res, onResId);
		}

		public Bitmap getOffBitmap(Resources res) {
			return BitmapFactory.decodeResource(res, offResId);
		}
	}

	public int getItemSize() {
		return mItems.size();
	}

	public int getSelectedItemId() {
		return mSelectedItemId == 0 ? mItems.keyAt(0) : mSelectedItemId;
	}

}
