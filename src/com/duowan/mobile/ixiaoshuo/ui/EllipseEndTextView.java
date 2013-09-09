package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.utils.StringUtil;

import java.util.ArrayList;

/**
 * This Class base on http://code.google.com/p/android-textview-multiline-ellipse
 * but origin version has some problem, so I did change it
 * @author Vince
 */
public class EllipseEndTextView extends View {
	private int mCollapseBackground;
	private int mLineSpacing;

	public EllipseEndTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.EllipseEndTextView);

		mStrEllipsis = "...";
		mMaxLineCount = typeArray.getInteger(R.styleable.EllipseEndTextView_maxLines, 5);
		mLineSpacing = typeArray.getDimensionPixelSize(R.styleable.EllipseEndTextView_lineSpacing, 0);

		mPaint = new TextPaint();
		mPaint.setColor(typeArray.getColor(R.styleable.EllipseEndTextView_textColor, Color.BLACK));
		mPaint.setTextSize(typeArray.getDimensionPixelSize(R.styleable.EllipseEndTextView_textSize, 0));
		mCollapseBackground = typeArray.getResourceId(R.styleable.EllipseEndTextView_collapseBackground, 0);
	}

	private int mAscent;
	private int mMaxLineCount;
	private int mDrawLineCount;

	private String mText;
	private TextPaint mPaint;

	private boolean mExpanded = false;
	private String mStrEllipsis;

	/** Beginning and end indices for the input string. */
	private ArrayList<int[]> mLines;

	public void setText(String text) {
		mText = text;
		requestLayout();
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
		if (mExpanded) {
			setBackgroundColor(Color.TRANSPARENT);
			setOnClickListener(null);
		} else {
			setBackgroundResource(mCollapseBackground);
			setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					expand();
				}
			});
		}
	}

	/**
	 * Determines the width of this view
	 * @param measureSpec A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// Format the text using this exact width, and the current mode.
			breakWidth(specSize);
			// We were told how big to be.
			return specSize;
		} else if (specMode == MeasureSpec.AT_MOST) {
			// Use the AT_MOST size - if we had very short text, we may need even less
			// than the AT_MOST value, so return the minimum.
			return Math.min(breakWidth(specSize), specSize);
		} else {
			// We're not given any width - so in this case we assume we have an unlimited width?
			return breakWidth(specSize);
		}
	}

	/**
	 * Determines the height of this view
	 * @param measureSpec A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		mAscent = (int) mPaint.ascent();
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be, so nothing to do.
			result = specSize;
		} else {
			// The lines should already be broken up. Calculate our max desired height
			// for our current mode.
			if (mExpanded) {
				mDrawLineCount = mLines.size();
			} else if (mLines.size() > mMaxLineCount) {
				mDrawLineCount = mMaxLineCount;
			} else {
				mDrawLineCount = mLines.size();
				mExpanded = true;
			}

			int textHeight = (int) (-mAscent + mPaint.descent());
			result = getPaddingTop() + getPaddingBottom();
			if (mDrawLineCount > 0) {
				result += mDrawLineCount * textHeight + (mDrawLineCount - 1) * mLineSpacing;
			} else {
				result += textHeight;
			}

			// Respect AT_MOST value if that was what is called for by measureSpec.
			if (specMode == MeasureSpec.AT_MOST) result = Math.min(result, specSize);
		}
		return result;
	}

	private int breakWidth(int availableWidth) {
		int maxWidth = availableWidth - getPaddingLeft() - getPaddingRight();
		mLines = new ArrayList<int[]>(mMaxLineCount * 2);
		int widthUsed;

		// If maxWidth is -1, interpret that as meaning to render the string on a single
		// line. Skip everything.
		if (maxWidth == -1) {
			mLines.add(new int[]{0, mText.length()});
			widthUsed = (int) (mPaint.measureText(mText) + 0.5f);
		} else {
			int index = 0;
			int newlineIndex = 0;
			int endCharIndex = 0;

			while (index < mText.length()) {
				if (index == newlineIndex) {
					newlineIndex = mText.indexOf(StringUtil.NEW_LINE_STR, newlineIndex);
					endCharIndex = (newlineIndex != -1) ? newlineIndex : mText.length();
				}

//				boolean needIndent = index == 0 || mText.charAt(index - 1) == StringUtil.NEW_LINE_CHAR;
				int charCount = mPaint.breakText(mText, index, endCharIndex, maxWidth, false);
				if (charCount > 0) {
					mLines.add(new int[]{index, index + charCount});
					index += charCount;
				}

				if (index == newlineIndex) {
					newlineIndex++;
					index++;
				}
			}

			// If we required only one line, return its length, otherwise we used
			// whatever the maxWidth supplied was.
			switch (mLines.size()) {
				case 1:
					widthUsed = (int) (mPaint.measureText(mText) + 0.5f);
					break;
				case 0:
					widthUsed = 0;
					break;
				default:
					widthUsed = maxWidth;
					break;
			}
		}

		return widthUsed + getPaddingLeft() + getPaddingRight();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int renderWidth = canvas.getWidth() - getPaddingLeft() - getPaddingRight();
		float x = getPaddingLeft();
		float y = getPaddingTop() - mAscent;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mDrawLineCount; i++) {
			sb.append(mText, mLines.get(i)[0], mLines.get(i)[1]);

			// Draw the ellipsis if necessary.
			if (!mExpanded && mDrawLineCount - i == 1) {
				float lineDrawWidth = mPaint.measureText(sb);
				float ellipsisWidth = mPaint.measureText(mStrEllipsis);
				while (lineDrawWidth + ellipsisWidth > renderWidth) {
					sb.deleteCharAt(sb.length() - 1);
					lineDrawWidth = mPaint.measureText(sb);
				}
				sb.append(mStrEllipsis);
			}

			// Draw the current line.
			canvas.drawText(sb, 0, sb.length(), x, y, mPaint);

			y += (-mAscent + mPaint.descent()) + mLineSpacing;
			if (y > canvas.getHeight()) break;
			sb.delete(0, sb.length());
		}
	}

	public boolean getIsExpanded() {
		return mExpanded;
	}

	public void expand() {
		mExpanded = true;
		requestLayout();
		invalidate();
	}

	public void collapse() {
		mExpanded = false;
		requestLayout();
		invalidate();
	}

}
