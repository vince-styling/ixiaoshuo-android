package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.vincestyling.ixiaoshuo.R;

public class ComplexBookNameView<T> extends View {

    public ComplexBookNameView(Context context) {
        this(context, null);
    }

    public ComplexBookNameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComplexBookNameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBoundsF = new RectF();
        mBounds = new Rect();

        mTextSize = getResources().getDimension(R.dimen.complex_bookname_txt);
        mTextColor = getResources().getColor(R.color.complex_bookname_txt);

        mFlagSplitPadding = getResources().getDimensionPixelSize(R.dimen.finder_booklist_bookname_flag_split_padding);
        mFlagMarginLeft = getResources().getDimensionPixelSize(R.dimen.finder_booklist_bookname_flag_marginleft);
        mFlagTextPadding = getResources().getDimensionPixelSize(R.dimen.finder_booklist_bookname_flag_padding);
        mFlagTextSize = getResources().getDimensionPixelSize(R.dimen.finder_booklist_bookname_flag);
        mFlagTextColor = getResources().getColor(R.color.finder_booklist_bookname_flag);
    }

    private int mFlagTextPadding, mFlagTextSize, mFlagTextColor, mFlagMarginLeft, mFlagSplitPadding;
    private int mFlagWidth, mFlagHeight;

    private static final String ELLIPSIZE_TEXT = "…";
    private float mTextSize, mTextWidth, mTextHeight;
    private boolean mShouldEllipsize;
    private int mTextColor;

    private Paint mPaint;
    private Rect mBounds;
    private RectF mBoundsF;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float measureWidth = 0;
        float measureHeight = 0;
        mFlagWidth = mFlagHeight = 0;

        if (mSatisficer.isFirstConditionSatisfy(mBook)) {
            String text = getResources().getString(mSatisficer.firstConditionLabel(mBook));
            mPaint.setTextSize(mFlagTextSize);

            mBoundsF.setEmpty();
            mBoundsF.right = mPaint.measureText(text);
            mBoundsF.bottom = mPaint.descent() - mPaint.ascent();

            measureWidth += mBoundsF.width() + mFlagTextPadding * 2;
            measureHeight += mBoundsF.height() + mFlagTextPadding * 2;
        }

        if (mSatisficer.isSecondConditionSatisfy(mBook)) {
            String text = getResources().getString(mSatisficer.secondConditionLabel(mBook));
            if (measureWidth == 0) mPaint.setTextSize(mFlagTextSize);

            mBoundsF.setEmpty();
            mBoundsF.right = mPaint.measureText(text);
            mBoundsF.bottom = mPaint.descent() - mPaint.ascent();

            measureWidth += mBoundsF.width() + mFlagTextPadding * 2;
            if (measureHeight == 0) measureHeight += mBoundsF.height() + mFlagTextPadding * 2;
        }

        if (mSatisficer.isFirstConditionSatisfy(mBook) && mSatisficer.isSecondConditionSatisfy(mBook)) {
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.drawable.red_flag_split, bitmapOptions);
            measureWidth += bitmapOptions.outWidth;
            measureWidth += mFlagSplitPadding * 2;
        }

        if (measureWidth > 0) {
            Drawable flagDrawable = getResources().getDrawable(R.drawable.red_flag);
            flagDrawable.getPadding(mBounds);

            measureWidth += mBounds.left + mBounds.right + mFlagMarginLeft;
            measureHeight += mBounds.top + mBounds.bottom;

            mFlagWidth = (int) measureWidth;
            mFlagHeight = (int) measureHeight;
        }

        mPaint.setTextSize(mTextSize);
        mBoundsF.setEmpty();
        mBoundsF.bottom = mPaint.descent() - mPaint.ascent();
        mBoundsF.right = mPaint.measureText(mSatisficer.getBookName(mBook));

        mTextWidth = mBoundsF.width();
        mTextHeight = mBoundsF.height();

        measureWidth += mTextWidth;
        measureHeight = mTextHeight + measureHeight * .5f;
        measureHeight += getPaddingTop() + getPaddingBottom();

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        mBoundsF.set(getPaddingLeft(), getPaddingTop(),
                widthSpecSize - getPaddingRight(), measureHeight - getPaddingBottom());
        mShouldEllipsize = measureWidth > mBoundsF.width();

        setMeasuredDimension(widthSpecSize, (int) measureHeight);
//		AppLog.e("Book : %s, width : %f height : %f", mSatisficer.getBookName(mBook), measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBook == null) return;

        canvas.clipRect(mBoundsF);


        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        StringBuilder mNameBuf = new StringBuilder(mSatisficer.getBookName(mBook));
        mBoundsF.top += mBoundsF.height() - mTextHeight - mPaint.ascent();

        if (mShouldEllipsize) {
            float ellipsisWidth = measureWordWidthWithoutGlyphValue(ELLIPSIZE_TEXT);
            float maxTextWidth = mBoundsF.width() - mFlagWidth;
            do {
                mNameBuf.deleteCharAt(mNameBuf.length() - 1);
                mTextWidth = mPaint.measureText(mNameBuf, 0, mNameBuf.length()) + ellipsisWidth;
            } while (mTextWidth > maxTextWidth);

            mNameBuf.append(ELLIPSIZE_TEXT);
        }

        canvas.drawText(mNameBuf, 0, mNameBuf.length(), mBoundsF.left, mBoundsF.top, mPaint);


        if (mFlagWidth == 0) return;
        canvas.translate(mBoundsF.left + mTextWidth + mFlagMarginLeft, getPaddingTop());

        mBounds.set(0, 0, mFlagWidth - mFlagMarginLeft, mFlagHeight);
        Drawable flagDrawable = getResources().getDrawable(R.drawable.red_flag);
        flagDrawable.setBounds(mBounds);
        flagDrawable.draw(canvas);

        flagDrawable.getPadding(mBounds);
        mBoundsF.left = mBounds.left + mFlagTextPadding;
        mBoundsF.right = mFlagWidth - mBounds.right;
        mBoundsF.top = mBounds.top;
        mBoundsF.bottom = mFlagHeight - mBounds.bottom;

        mPaint.setColor(mFlagTextColor);
        mPaint.setTextSize(mFlagTextSize);

        String text = null;
        if (mSatisficer.isFirstConditionSatisfy(mBook)) {
            text = getResources().getString(mSatisficer.firstConditionLabel(mBook));
            canvas.drawText(text, mBoundsF.left, mBoundsF.top + mFlagTextPadding - mPaint.ascent(), mPaint);
        }

        if (mSatisficer.isSecondConditionSatisfy(mBook)) {
            if (mSatisficer.isFirstConditionSatisfy(mBook)) {
                mBoundsF.left += mPaint.measureText(text) + mFlagTextPadding + mFlagSplitPadding;
                canvas.clipRect(mBoundsF);

                Bitmap splitBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_flag_split);
                float addedTop = 0;
                do {
                    canvas.drawBitmap(splitBitmap, mBoundsF.left, mBoundsF.top + addedTop, mPaint);
                    addedTop += splitBitmap.getHeight();
                } while (mBoundsF.height() > addedTop);

                mBoundsF.left += splitBitmap.getWidth() + mFlagSplitPadding + mFlagTextPadding;
            }

            text = getResources().getString(mSatisficer.secondConditionLabel(mBook));
            canvas.drawText(text, mBoundsF.left, mBoundsF.top + mFlagTextPadding - mPaint.ascent(), mPaint);
        }
    }

    // difference between .measureText() and .getTextBounds() : http://stackoverflow.com/a/7579469/1294681
    private int measureWordWidthWithoutGlyphValue(String text) {
        mBounds.setEmpty();
        mPaint.getTextBounds(text, 0, text.length(), mBounds);
        return mBounds.width();
    }

    private T mBook;

    public void setBook(T book) {
        mBook = book;
        requestLayout();
        invalidate();
    }

    private ConditionSatisficer<T> mSatisficer;

    public void setSatisficer(ConditionSatisficer<T> satisficer) {
        if (mSatisficer == null) mSatisficer = satisficer;
    }

    public interface ConditionSatisficer<T> {
        String getBookName(T book);

        boolean isFirstConditionSatisfy(T book);

        int firstConditionLabel(T book);

        boolean isSecondConditionSatisfy(T book);

        int secondConditionLabel(T book);
    }
}
