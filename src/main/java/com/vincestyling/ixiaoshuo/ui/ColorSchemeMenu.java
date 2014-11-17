package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.pojo.ColorScheme;
import com.vincestyling.ixiaoshuo.reader.ReaderActivity;

import java.lang.ref.WeakReference;

public class ColorSchemeMenu extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mSchemeCount, mSchemeWidth, mSchemeHeight, mSchemeSelectedColor;
    private int mSchemeTextMargin, mSchemeTextSize, mSchemeTextColor;
    private int mSchemeMargin, mSchemeCornerRadius, mBorderSize;

    private int mTempSelectedItemIndex = -1, mSelectedItemIndex = -1;
    private WeakReference<Bitmap> mItemsBitmapRef;

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

        mSchemeCount = getActivity().getPreferences().getColorSchemes().length;

        mSelectedItemIndex = getActivity().getPreferences().getColorSchemeIndex();
        mTempSelectedItemIndex = mSelectedItemIndex;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = mSchemeWidth * mSchemeCount + mSchemeMargin * (mSchemeCount - 1);
        int measureHeight = mSchemeHeight + mSchemeTextMargin + mSchemeTextSize;
        setMeasuredDimension(measureWidth, measureHeight);
    }

    private void init() {
        Bitmap itemsBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mItemsBitmapRef = new WeakReference<Bitmap>(itemsBitmap);
        Canvas canvas = new Canvas(itemsBitmap);
        for (int index = 0; index < mSchemeCount; index++) {
            drawItem(canvas, index, false);
        }
    }

    private void drawItem(Canvas canvas, int index, boolean highlighting) {
        Bitmap schemeBitmap = getActivity().getPreferences().getColorSchemes()[index]
                .getResizeSchemeBitmap(getResources(), mSchemeWidth, mSchemeHeight);
        RectF itemRect = getItemRect(index);

        itemRect.bottom = itemRect.top + mSchemeHeight;
        RectF roundRect = new RectF(itemRect);

//        int origColor = mPaint.getColor();

        // -------------------------------------- draw selected background
        if (highlighting && index == mTempSelectedItemIndex) {
//            Paint.Style origStyle = mPaint.getStyle();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mSchemeSelectedColor);
            canvas.drawRoundRect(roundRect, mSchemeCornerRadius, mSchemeCornerRadius, mPaint);
//            mPaint.setStyle(origStyle);
//            mPaint.setColor(origColor);
        }

        // -------------------------------------- draw scheme bitmap
        roundRect.inset(mBorderSize, mBorderSize);
        mPaint.setShader(new BitmapShader(schemeBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(roundRect, mSchemeCornerRadius, mSchemeCornerRadius, mPaint);
        mPaint.setShader(null);

        // -------------------------------------- draw scheme name
//        float origTextSize = mPaint.getTextSize();
        mPaint.setTextSize(mSchemeTextSize);
        Rect textBoundsRect = new Rect();
        ColorScheme scheme = getActivity().getPreferences().getColorSchemes()[index];
        mPaint.getTextBounds(scheme.getName(), 0, scheme.getName().length(), textBoundsRect);
        float left = itemRect.left + (itemRect.width() - textBoundsRect.width()) / 2;
        float top = itemRect.bottom + mSchemeTextMargin - mPaint.ascent();

        mPaint.setColor(highlighting ? mSchemeSelectedColor : mSchemeTextColor);
        canvas.drawText(scheme.getName(), left, top, mPaint);

//        mPaint.setTextSize(origTextSize);
//        mPaint.setColor(origColor);
    }

    private RectF getItemRect(int index) {
        RectF itemRect = new RectF();
        itemRect.left = (index % mSchemeCount) * (mSchemeWidth + mSchemeMargin);
        itemRect.right = itemRect.left + mSchemeWidth;
        itemRect.bottom = getHeight();
        return itemRect;
    }

    /**
     * Notice : Never use onDraw's Canvas do any complex work such as drawRoundRect or setShader,
     * it's problematical between difference Canvas instance :
     * android.graphics.Canvas VS android.view.GLES20RecordingCanvas.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (mItemsBitmapRef == null || mItemsBitmapRef.get() == null) init();
        canvas.drawBitmap(mItemsBitmapRef.get(), 0, 0, mPaint);
        drawItem(canvas, mTempSelectedItemIndex, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int index = 0; index < mSchemeCount; index++) {
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

    public ReaderActivity getActivity() {
        return (ReaderActivity) getContext();
    }
}
