package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.pojo.ColorScheme;
import com.vincestyling.ixiaoshuo.reader.ReaderActivity;

public class ColorSchemeMenu extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Bitmap[] mSchemeBitmaps;
    private int mColumnCount;
    private int mSelectedItemIndex = -1;
    private int mTempSelectedItemIndex = -1;

    private int mSchemeCornerRadius, mBorderSize;
    private int mSchemeWidth, mSchemeHeight;
    private int mSchemeSelectedColor;
    private int mSchemeMargin;

    private int mSchemeTextMargin;
    private int mSchemeTextSize;
    private int mSchemeTextColor;

    public ColorSchemeMenu(Context context, AttributeSet attrs) {
        super(context, attrs);

        mSchemeCornerRadius = getResources().getDimensionPixelSize(R.dimen.reading_menu_scheme_corner_radius);
        mBorderSize = getResources().getDimensionPixelSize(R.dimen.reading_menu_scheme_border_size);
        mSchemeWidth = getResources().getDimensionPixelSize(R.dimen.reading_menu_scheme_width);
        mSchemeHeight = getResources().getDimensionPixelSize(R.dimen.reading_menu_scheme_height);
        mSchemeMargin = getResources().getDimensionPixelSize(R.dimen.reading_menu_scheme_margin);
        mSchemeSelectedColor = getResources().getColor(R.color.reading_menu_scheme_selected_color);

        mSchemeTextMargin = getResources().getDimensionPixelSize(R.dimen.reading_menu_scheme_text_margin);
        mSchemeTextSize = getResources().getDimensionPixelSize(R.dimen.reading_menu_scheme_text_size);
        mSchemeTextColor = getResources().getColor(R.color.reading_menu_scheme_text_color);

        mColumnCount = getActivity().getPreferences().getColorSchemes().length;
        mSchemeBitmaps = new Bitmap[mColumnCount];

        mSelectedItemIndex = getActivity().getPreferences().getColorSchemeIndex();
        mTempSelectedItemIndex = mSelectedItemIndex;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = mSchemeWidth * mColumnCount + mSchemeMargin * (mColumnCount - 1);
        int measureHeight = mSchemeHeight + mSchemeTextMargin + mSchemeTextSize;
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int index = 0; index < mColumnCount; index++) {
            if (mSchemeBitmaps[index] == null) {
                ColorScheme scheme = getActivity().getPreferences().getColorSchemes()[index];
                mSchemeBitmaps[index] = scheme.getResizeSchemeBitmap(getResources(), mSchemeWidth, mSchemeHeight);
            }
            canvas.save(Canvas.CLIP_SAVE_FLAG);
            RectF itemRect = getItemRect(index);
            canvas.clipRect(itemRect);
            drawItem(canvas, itemRect, index);
            canvas.restore();
        }
    }

    private RectF getItemRect(int index) {
        RectF itemRect = new RectF();
        itemRect.left = (index % mColumnCount) * (mSchemeWidth + mSchemeMargin);
        itemRect.right = itemRect.left + mSchemeWidth;
        itemRect.bottom = getHeight();
        return itemRect;
    }

    private void drawItem(Canvas canvas, RectF itemRect, int index) {
        ColorScheme scheme = getActivity().getPreferences().getColorSchemes()[index];
        itemRect.bottom = itemRect.top + mSchemeHeight;
        RectF roundRect = new RectF(itemRect);

        // ------------------ draw selected background
        if (index == mTempSelectedItemIndex) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mSchemeSelectedColor);
            canvas.drawRoundRect(roundRect, mSchemeCornerRadius, mSchemeCornerRadius, mPaint);
        }

        // ------------------ draw scheme bitmap
        roundRect.set(itemRect.left + mBorderSize, itemRect.top + mBorderSize, itemRect.right - mBorderSize, itemRect.bottom - mBorderSize);
        Shader shader = new BitmapShader(mSchemeBitmaps[index], Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setShader(shader);
        canvas.drawRoundRect(roundRect, mSchemeCornerRadius, mSchemeCornerRadius, mPaint);
        mPaint.setShader(null);

        // ------------------ draw scheme name
        mPaint.setTextSize(mSchemeTextSize);
        mPaint.setColor(mSchemeTextColor);
        Rect textBoundsRect = new Rect();
        mPaint.getTextBounds(scheme.getName(), 0, scheme.getName().length(), textBoundsRect);
        float left = itemRect.left + (itemRect.width() - textBoundsRect.width()) / 2;
        float top = itemRect.bottom + mSchemeTextMargin - mPaint.ascent();
        canvas.drawText(scheme.getName(), left, top, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int index = 0; index < mColumnCount; index++) {
                    RectF itemRect = getItemRect(index);
                    if (itemRect.contains(event.getX(), event.getY())) {
                        if (index == mTempSelectedItemIndex) return false;
                        mTempSelectedItemIndex = index;
                        invalidate();
                        break;
                    }
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (getItemRect(mTempSelectedItemIndex).contains(event.getX(), event.getY())) return true;
                mTempSelectedItemIndex = mSelectedItemIndex;
                invalidate();
                return false;
            case MotionEvent.ACTION_UP:
                if (mSelectedItemIndex != mTempSelectedItemIndex) {
                    mSelectedItemIndex = mTempSelectedItemIndex;
                    getActivity().getPreferences().setColorSchemeIndex(mSelectedItemIndex);
                    getActivity().onChangeColorScheme();
                }
                return false;
        }
        return true;
    }

    public final ReaderActivity getActivity() {
        return (ReaderActivity) getContext();
    }

}
