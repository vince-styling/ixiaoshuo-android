package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.WindowManager;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.doc.LayoutUtil;
import com.duowan.mobile.ixiaoshuo.utils.BitmapUtil;

public class RenderPaint extends Paint {
	private Drawable mBgDrawable;

	private int mRenderWidth, mRenderHeight;
	private int mLineSpacing, mParagraphSpacing;
	private int mCanvasPaddingLeft, mCanvasPaddingTop, mCanvasPaddingBottom;
	private int mTextWidth, mTextHeight;
	private int mStatusBarHeight;
	private String mFirstLineIndent = "";

	public RenderPaint(Context ctx) {
		super(ANTI_ALIAS_FLAG);
		Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Resources res = ctx.getResources();

		mLineSpacing = res.getDimensionPixelSize(R.dimen.reading_board_line_spacing);
		mParagraphSpacing = res.getDimensionPixelSize(R.dimen.reading_board_paragraph_spacing);

		mStatusBarHeight = res.getDimensionPixelSize(R.dimen.reading_board_status_bar_height);

		mCanvasPaddingLeft = res.getDimensionPixelSize(R.dimen.reading_board_padding_left);
		mCanvasPaddingTop = res.getDimensionPixelSize(R.dimen.reading_board_padding_top);
		mCanvasPaddingBottom = res.getDimensionPixelSize(R.dimen.reading_board_padding_bottom);

		mRenderWidth = display.getWidth() - mCanvasPaddingLeft * 2;
		mRenderHeight = display.getHeight() - mCanvasPaddingTop - mCanvasPaddingBottom - mStatusBarHeight;

		Bitmap readingBg = BitmapUtil.loadBitmapInRes(res, R.drawable.reading_bg_1, display.getWidth(), display.getHeight());
		mBgDrawable = new BitmapDrawable(readingBg);
		mBgDrawable.setBounds(new Rect(0, 0, display.getWidth(), display.getHeight()));

		setColor(0xff543927);
		setTextSize(res.getDimensionPixelSize(R.dimen.reading_board_default_text_size));

		int indentCharCount = res.getInteger(R.integer.reading_board_indent_char_count);
		for (int i = 0; i < indentCharCount; i++) {
			mFirstLineIndent += LayoutUtil.SBC_CASE_SPACE_CHAR;
		}
	}

	public void drawBg(Canvas canvas) {
		mBgDrawable.draw(canvas);
	}

	@Override
	public void setTextSize(float textSize) {
		super.setTextSize(textSize);
		Rect rect = new Rect();
		getTextBounds("中", 0, 1, rect);
		mTextWidth = rect.right - rect.left;
		mTextHeight = rect.bottom - rect.top;
	}

	public int breakText(StringBuilder contentBuf, int startIndex, int endIndex, boolean needIndent) {
		float renderWidth = needIndent ? mRenderWidth - measureText(mFirstLineIndent) : mRenderWidth;
		int suggestCharCount = breakText(contentBuf, startIndex, endIndex, true, renderWidth, null);
		int lineEndIndex = startIndex + suggestCharCount;
		if (lineEndIndex < contentBuf.length()) {
			char nextChar = contentBuf.charAt(lineEndIndex);
			if (LayoutUtil.isPunctuation(nextChar)) {
				while (--lineEndIndex > startIndex) {    // at least one character will be left
					char ch = contentBuf.charAt(lineEndIndex);
					if (!LayoutUtil.isSBCPunctuation(ch) && !LayoutUtil.isASCII(ch)) {
						return lineEndIndex - startIndex;
					}
				}
			} else if (LayoutUtil.isAlphanumeric(nextChar)) {
				while (--lineEndIndex > startIndex) {    // at least one character will be left
					if (!LayoutUtil.isAlphanumeric(contentBuf.charAt(lineEndIndex))) {
						return lineEndIndex + 1 - startIndex;
					}
				}
			}
		}
		return suggestCharCount;
	}

	public int getTextWidth() {
		return mTextWidth;
	}

	public int getTextHeight() {
		return mTextHeight;
	}

	public int getRenderWidth() {
		return mRenderWidth;
	}

	public int getRenderHeight() {
		return mRenderHeight;
	}

	public int getLineSpacing() {
		return mLineSpacing;
	}

	public int getParagraphSpacing() {
		return mParagraphSpacing;
	}

	public int getCanvasPaddingLeft() {
		return mCanvasPaddingLeft;
	}

	public int getCanvasPaddingTop() {
		return mCanvasPaddingTop;
	}

	public int getCanvasPaddingBottom() {
		return mCanvasPaddingBottom;
	}

	public String getFirstLineIndent() {
		return mFirstLineIndent;
	}

}
