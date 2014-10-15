package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.WindowManager;
import com.vincestyling.ixiaoshuo.R;

public class RenderPaint extends PlainTextPaint {
    private static RenderPaint mInstance;

    public static RenderPaint get() {
        return mInstance;
    }

    public static void init(Context ctx) {
        mInstance = new RenderPaint(ctx);
    }

    public static void destory() {
        mInstance = null;
    }

    private Drawable mReadingBg;
    private int mLineSpacing, mParagraphSpacing;
    private int mRenderWidth, mRenderHeight;
    private int mCanvasPaddingLeft, mCanvasPaddingTop;
    private int mMaxPageLineCount;
    protected int mMaxCharCountPerPage;
    private int mMaxCharCountPerLine;

    private RenderPaint(Context ctx) {
        super();
        Resources res = ctx.getResources();

        mParagraphSpacing = res.getDimensionPixelSize(R.dimen.reading_board_paragraph_spacing);
        mLineSpacing = res.getDimensionPixelSize(R.dimen.reading_board_line_spacing);

        int statusBarHeight = res.getDimensionPixelSize(R.dimen.reading_board_status_bar_height);
        mCanvasPaddingLeft = res.getDimensionPixelSize(R.dimen.reading_board_padding_left);
        mCanvasPaddingTop = res.getDimensionPixelSize(R.dimen.reading_board_padding_top);

        Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        mRenderHeight = display.getHeight() - mCanvasPaddingTop - statusBarHeight;
        mRenderWidth = display.getWidth() - mCanvasPaddingLeft * 2;

        setFirstLineIndentCharCount(res.getInteger(R.integer.reading_board_indent_char_count));
    }

    public void drawReadingBg(Canvas canvas) {
        mReadingBg.draw(canvas);
    }

    @Override
    public void setTextSize(float textSize) {
        super.setTextSize(textSize);

        float lineHeight = getTextHeight() + getLineSpacing();
        mMaxPageLineCount = (int) (getRenderHeight() / lineHeight);
        if ((getRenderHeight() % lineHeight) >= getTextHeight()) {
            ++mMaxPageLineCount;
        }

        mMaxCharCountPerLine = getRenderWidth() / getTextWidth();

        Rect rect = new Rect();
        getTextBounds("i", 0, 1, rect);
        mMaxCharCountPerPage = getRenderWidth() / (rect.right - rect.left) * mMaxPageLineCount;
    }

    public int breakText(StringBuilder contentBuf, int startIndex, int endIndex, boolean needIndent) {
        return super.breakText(contentBuf, startIndex, endIndex, mRenderWidth, needIndent);
    }

    public void setReadingBg(Drawable readingBg) {
        mReadingBg = readingBg;
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

    public int getMaxPageLineCount() {
        return mMaxPageLineCount;
    }

    public int getMaxCharCountPerPage() {
        return mMaxCharCountPerPage;
    }

    public int getMaxCharCountPerLine() {
        return mMaxCharCountPerLine;
    }

}
