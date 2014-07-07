package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.pojo.Book;

public class ComplexBookNameView extends View {

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

		mTextSize = getResources().getDimension(R.dimen.finder_booklist_bookname);
		mTextColor = getResources().getColor(R.color.finder_booklist_bookname);

		mFlagSplitPadding = getResources().getDimensionPixelSize(R.dimen.finder_booklist_bookname_flag_split_padding);
		mFlagMarginLeft = getResources().getDimensionPixelSize(R.dimen.finder_booklist_bookname_flag_marginleft);
		mFlagTextPadding = getResources().getDimensionPixelSize(R.dimen.finder_booklist_bookname_flag_padding);
		mFlagTextSize = getResources().getDimensionPixelSize(R.dimen.finder_booklist_bookname_flag);
		mFlagTextColor = getResources().getColor(R.color.finder_booklist_bookname_flag);
	}

	private StringBuilder mNameBuf;

	private float mMeasureWidth, mMeasureHeight;

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
		mMeasureWidth = mMeasureHeight = 0;

		if (mBook.isFinished()) measureOneWord();

		if (mBook.isBothType()) {
			if (mMeasureWidth == 0) measureOneWord();
			else mMeasureWidth *= 2;
		}

		if (mBook.isFinished() && mBook.isBothType()) {
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(getResources(), R.drawable.red_flag_split, bitmapOptions);
			mMeasureWidth += bitmapOptions.outWidth;
			mMeasureWidth += mFlagSplitPadding * 2;
		}

		if (mMeasureWidth > 0) {
			Drawable flagDrawable = getResources().getDrawable(R.drawable.red_flag);
			flagDrawable.getPadding(mBounds);

			mMeasureWidth += mBounds.left + mBounds.right + mFlagMarginLeft;
			mMeasureHeight += mBounds.top + mBounds.bottom;

			mFlagWidth = (int) mMeasureWidth;
			mFlagHeight = (int) mMeasureHeight;
		}

		mNameBuf.append(mBook.getName());
		mPaint.setTextSize(mTextSize);
		mBoundsF.setEmpty();
		mBoundsF.bottom = mPaint.descent() - mPaint.ascent();
		mBoundsF.right = mPaint.measureText(mNameBuf, 0, mNameBuf.length());
		mNameBuf.setLength(0);

		mTextWidth = mBoundsF.width();
		mTextHeight = mBoundsF.height();

		mMeasureWidth += mTextWidth;
		mMeasureHeight = mTextHeight + mMeasureHeight * .8f;
		mMeasureHeight += getPaddingTop() + getPaddingBottom();

		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		mShouldEllipsize = mMeasureWidth > widthSpecSize;

		setMeasuredDimension(widthSpecSize, (int) mMeasureHeight);
//		log(String.format("width : %f height : %f", mMeasureWidth, mMeasureHeight));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mBook == null) return;

		mBoundsF.set(getPaddingLeft(), getPaddingTop(),
				getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
		canvas.clipRect(mBoundsF);


		mPaint.setColor(mTextColor);
		mPaint.setTextSize(mTextSize);
		mNameBuf.append(mBook.getName());
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
		mNameBuf.setLength(0);


		if (!mBook.isFinished() && !mBook.isBothType()) return;

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
		if (mBook.isFinished()) {
			text = getResources().getString(R.string.book_status_tip_finished);
			canvas.drawText(text, mBoundsF.left, mBoundsF.top + mFlagTextPadding - mPaint.ascent(), mPaint);
		}

		if (mBook.isBothType()) {
			if (mBook.isFinished()) {
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

			text = getResources().getString(R.string.book_status_tip_both_type);
			canvas.drawText(text, mBoundsF.left, mBoundsF.top + mFlagTextPadding - mPaint.ascent(), mPaint);
		}
	}

	private void measureOneWord() {
		String text = getResources().getString(R.string.book_status_tip_finished);
		mPaint.setTextSize(mFlagTextSize);

		mBoundsF.setEmpty();
		mBoundsF.bottom = mPaint.descent() - mPaint.ascent();
		mBoundsF.right = mPaint.measureText(text);

		mMeasureWidth += mBoundsF.width();
		mMeasureHeight += mBoundsF.height();

		mMeasureWidth += mFlagTextPadding * 2;
		mMeasureHeight += mFlagTextPadding * 2;
	}

	// difference between .measureText() and .getTextBounds() : http://stackoverflow.com/a/7579469/1294681
	private int measureWordWidthWithoutGlyphValue(String text) {
		mBounds.setEmpty();
		mPaint.getTextBounds(text, 0, text.length(), mBounds);
		return mBounds.width();
	}

	private Book mBook;

	public void setBook(Book book) {
		mNameBuf = new StringBuilder(book.getName().length());
		mBook = book;
		invalidate();
	}

}
