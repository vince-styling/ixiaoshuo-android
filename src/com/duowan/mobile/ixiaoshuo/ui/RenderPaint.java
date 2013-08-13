package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.Display;
import android.view.WindowManager;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.doc.LayoutUtil;
import com.duowan.mobile.ixiaoshuo.pojo.ColorScheme;

public class RenderPaint extends Paint {
	private ColorScheme mColorScheme;
	private int mRenderWidth, mRenderHeight;
	private int mLineSpacing, mParagraphSpacing;
	private int mCanvasPaddingLeft, mCanvasPaddingTop;
	private int mTextWidth, mTextHeight;

	private String mFirstLineIndent = "";
	private float mFirstLineIndentWidth;

	public RenderPaint(Context ctx) {
		super(ANTI_ALIAS_FLAG);
		Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Resources res = ctx.getResources();

		mParagraphSpacing = res.getDimensionPixelSize(R.dimen.reading_board_paragraph_spacing);
		mLineSpacing = res.getDimensionPixelSize(R.dimen.reading_board_line_spacing);

		int statusBarHeight = res.getDimensionPixelSize(R.dimen.reading_board_status_bar_height);
		mCanvasPaddingLeft = res.getDimensionPixelSize(R.dimen.reading_board_padding_left);
		mCanvasPaddingTop = res.getDimensionPixelSize(R.dimen.reading_board_padding_top);

		mRenderHeight = display.getHeight() - mCanvasPaddingTop - statusBarHeight;
		mRenderWidth = display.getWidth() - mCanvasPaddingLeft * 2;

		int indentCharCount = res.getInteger(R.integer.reading_board_indent_char_count);
		for (int i = 0; i < indentCharCount; i++) {
			mFirstLineIndent += LayoutUtil.SBC_CASE_SPACE_CHAR;
		}

		setTextSize(res.getDimensionPixelSize(R.dimen.reading_board_default_text_size));
	}

	public void drawBg(Canvas canvas) {
		mColorScheme.getReadingDrawable().draw(canvas);
	}

	@Override
	public void setTextSize(float textSize) {
		super.setTextSize(textSize);

		Rect rect = new Rect();
		getTextBounds("中", 0, 1, rect);
		mTextWidth = rect.right - rect.left;
		mTextHeight = rect.bottom - rect.top;

		if (mFirstLineIndent.length() > 0) {
			mFirstLineIndentWidth = measureText(mFirstLineIndent);
		}
	}

	public void setColorScheme(ColorScheme colorScheme) {
		setColor(colorScheme.getTextColor());
		mColorScheme = colorScheme;
	}

	public int breakText(StringBuilder contentBuf, int startIndex, int endIndex, boolean needIndent) {
		float renderWidth = needIndent ? mRenderWidth - mFirstLineIndentWidth : mRenderWidth;
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

	public String getFirstLineIndent() {
		return mFirstLineIndent;
	}

}
