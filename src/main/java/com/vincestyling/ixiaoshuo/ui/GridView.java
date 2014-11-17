package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.event.OnGridItemClickListener;

import java.lang.ref.WeakReference;

public class GridView extends View {

    public GridView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.GridView);
        mColumnCount = typeArray.getInteger(R.styleable.GridView_columnCount, 0);
        typeArray.recycle();

        mItems = new SparseArray<GridItem>();
        initGrid();
    }

    private SparseArray<GridItem> mItems;

    // we don't specify LineCount, because we use ColumnCount to calculate it
    private int mColumnCount;

    // the selected itemId
    private int mSelectedItemId = -1;

    // the selected temp itemId, when TouchEvent.onDown use
    private int mTempSelectedItemId = -1;

    // highlight drawable, must not transparent, allows nine-patch or normal image
    protected Drawable mHighlightDrawable;

    // this ItemsBitmap draw with all OFF status grid item, just draw once then reuse
    private WeakReference<Bitmap> mItemsBitmapRef;

    private int mItemWidth, mItemHeight;

    private synchronized void init() {
        int width = getWidth();
        int height = getHeight();

        // if not specify ColumnCount, we'll use Items size
        if (mColumnCount == 0) mColumnCount = mItems.size();
        mItemWidth = (int) Math.ceil((width) / mColumnCount);
        mItemHeight = height / (mItems.size() / mColumnCount);

        Bitmap itemsBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mItemsBitmapRef = new WeakReference<Bitmap>(itemsBitmap);
        itemsBitmap.eraseColor(Color.TRANSPARENT);

        Canvas canvas = new Canvas(itemsBitmap);
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
        int index = mItems.indexOfKey(mTempSelectedItemId);
        if (index < 0) return;

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
        if (mItemsBitmapRef == null || mItemsBitmapRef.get() == null) init();
        canvas.drawBitmap(mItemsBitmapRef.get(), 0, 0, null);
        highlightItem(canvas);
    }

    protected void initGrid() {}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int index = 0; index < mItems.size(); index++) {
                    Rect itemRect = getItemRect(index);
                    if (itemRect.contains((int) event.getX(), (int) event.getY())) {
                        int itemId = mItems.keyAt(index);
                        if (itemId == mTempSelectedItemId) return true;
                        mTempSelectedItemId = itemId;
                        invalidate();
                        break;
                    }
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                int index = mItems.indexOfKey(mTempSelectedItemId);
                if (getItemRect(index).contains((int) event.getX(), (int) event.getY())) return true;
                mTempSelectedItemId = mSelectedItemId;
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                if (mSelectedItemId != mTempSelectedItemId) {
                    mSelectedItemId = mTempSelectedItemId;
                    mItems.get(mSelectedItemId).performClick();
                    afterEvent();
                }
                return true;
        }
        return true;
    }

    protected void afterEvent() {
    }

    public void putItem(GridItem gridItem) {
        mItems.put(gridItem.getItemId(), gridItem);
    }

    public boolean selectItem(int itemId) {
        if (mSelectedItemId != itemId) {
            mTempSelectedItemId = itemId;
            mSelectedItemId = itemId;
            invalidate();
            return true;
        }
        return false;
    }

    public static class GridItem {
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

    public void setHighlightDrawable(Drawable highlightDrawable) {
        mHighlightDrawable = highlightDrawable;
    }
}
