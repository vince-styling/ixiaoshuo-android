package com.vincestyling.ixiaoshuo.ui;

import android.graphics.Paint;
import android.graphics.Rect;
import com.vincestyling.ixiaoshuo.doc.LayoutUtil;

public class PlainTextPaint extends Paint {

    public PlainTextPaint() {
        super(ANTI_ALIAS_FLAG);
    }

    private int mTextWidth, mTextHeight;

    private String mFirstLineIndent = "";
    private float mFirstLineIndentWidth;

    public void setFirstLineIndentCharCount(int indentCharCount) {
        for (int i = 0; i < indentCharCount; i++) {
            mFirstLineIndent += LayoutUtil.SBC_CASE_SPACE_CHAR;
        }
    }

    public String getFirstLineIndent() {
        return mFirstLineIndent;
    }

    public int getTextWidth() {
        return mTextWidth;
    }

    public int getTextHeight() {
        return mTextHeight;
    }

    @Override
    public void setTextSize(float textSize) {
        super.setTextSize(textSize);

        Rect rect = new Rect();
        getTextBounds("中", 0, 1, rect);
        mTextWidth = rect.width();
        mTextHeight = rect.height();

        if (mFirstLineIndent.length() > 0) {
            mFirstLineIndentWidth = measureText(mFirstLineIndent);
        }
    }

    public int breakText(CharSequence content, int startIndex, int endIndex, int width, boolean needIndent) {
        float renderWidth = needIndent ? width - mFirstLineIndentWidth : width;
        int suggestCharCount = breakText(content, startIndex, endIndex, true, renderWidth, null);
        int lineEndIndex = startIndex + suggestCharCount;
        if (lineEndIndex < content.length()) {
            char nextChar = content.charAt(lineEndIndex);
            if (LayoutUtil.isPunctuation(nextChar)) {
                while (--lineEndIndex > startIndex) {    // at least one character will be left
                    char ch = content.charAt(lineEndIndex);
                    if (!LayoutUtil.isSBCPunctuation(ch) && !LayoutUtil.isASCII(ch)) {
                        return lineEndIndex - startIndex;
                    }
                }
            } else if (LayoutUtil.isAlphanumeric(nextChar)) {
                while (--lineEndIndex > startIndex) {    // at least one character will be left
                    if (!LayoutUtil.isAlphanumeric(content.charAt(lineEndIndex))) {
                        return lineEndIndex + 1 - startIndex;
                    }
                }
            }
        }
        return suggestCharCount;
    }

    public float measureText(CharSequence text) {
        return measureText(text, 0, text.length());
    }

}
