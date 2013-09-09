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

public class RenderPaint extends TextPaint {
	private ColorScheme mColorScheme;
	private int mRenderWidth, mRenderHeight;
	private int mLineSpacing, mParagraphSpacing;
	private int mCanvasPaddingLeft, mCanvasPaddingTop;

	public RenderPaint(Context ctx) {
		super();

		Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Resources res = ctx.getResources();

		mParagraphSpacing = res.getDimensionPixelSize(R.dimen.reading_board_paragraph_spacing);
		mLineSpacing = res.getDimensionPixelSize(R.dimen.reading_board_line_spacing);

		int statusBarHeight = res.getDimensionPixelSize(R.dimen.reading_board_status_bar_height);
		mCanvasPaddingLeft = res.getDimensionPixelSize(R.dimen.reading_board_padding_left);
		mCanvasPaddingTop = res.getDimensionPixelSize(R.dimen.reading_board_padding_top);

		mRenderHeight = display.getHeight() - mCanvasPaddingTop - statusBarHeight;
		mRenderWidth = display.getWidth() - mCanvasPaddingLeft * 2;

		setFirstLineIndentCharCount(res.getInteger(R.integer.reading_board_indent_char_count));

		setTextSize(res.getDimensionPixelSize(R.dimen.reading_board_default_text_size));
	}

	public void drawBg(Canvas canvas) {
		mColorScheme.getReadingDrawable().draw(canvas);
	}

	public void setColorScheme(ColorScheme colorScheme) {
		setColor(colorScheme.getTextColor());
		mColorScheme = colorScheme;
	}

	public int breakText(StringBuilder contentBuf, int startIndex, int endIndex, boolean needIndent) {
		return super.breakText(contentBuf, startIndex, endIndex, mRenderWidth, needIndent);
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

}
