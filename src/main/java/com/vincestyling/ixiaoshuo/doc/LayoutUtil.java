package com.vincestyling.ixiaoshuo.doc;

import com.vincestyling.ixiaoshuo.ui.RenderPaint;

public class LayoutUtil {
    private final static char SPACE_CHAR = ' ';    // \u0020
    public final static char SBC_CASE_SPACE_CHAR = '　';    // \u3000

    public static boolean isASCII(char ch) {
        return ch <= 0x7f && ch != 0x20;
    }

    public static void trimWhiteSpaces(StringBuilder sb) {
        if (sb.length() == 0) return;

        // ----------- trim left
        int index = -1;
        while (++index < sb.length()) {
            char ch = sb.charAt(index);
            if (ch == SPACE_CHAR) continue;
            if (ch == SBC_CASE_SPACE_CHAR) continue;
            break;
        }
        if (index > 0) sb.delete(0, index);

        // ----------- trim right
        index = sb.length();
        while (--index >= 0) {
            char ch = sb.charAt(index);
            if (ch == SPACE_CHAR) continue;
            if (ch == SBC_CASE_SPACE_CHAR) continue;
            break;
        }
        if (++index < sb.length()) sb.delete(index, sb.length());
    }

    public static boolean isAlphanumeric(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9');
    }

    public static boolean isSBCPunctuation(char ch) {
        switch (ch) {
            case '！':
            case '？':
            case '“':    // opening double quote
            case '”':    // closing double quote
            case '：':
            case '‘':    // opening single quote
            case '’':    // closing single quote
            case '，':
            case '。':
            case '；':
            case '…':
            case '、':
                return true;
        }
        return false;
    }

    public static boolean isPunctuation(char ch) {
        switch (ch) {
            case '!':
            case '！':
            case '?':
            case '？':
            case '"':
            case '“':    // opening double quote
            case '”':    // closing double quote
            case ':':
            case '：':
            case '\'':
            case '‘':    // opening single quote
            case '’':    // closing single quote
            case ',':
            case '，':
            case '.':
            case '。':
            case ';':
            case '；':
            case '…':
            case '、':
                return true;
        }
        return false;
    }

    public static void layoutTextJustified(CharSequence text, RenderPaint paint, float startX, float constantY, float[] positions) {
        int endIndex = text.length();
        float actualWidth = paint.measureText(text, 0, text.length());

        // A glyphRun(concept in text rendering) contains one or more glyphs.
        // For SBC_CASE_SPACE_CHARs, I don't increase their kerning, because in chinese text,
        // SBC_CASE_SPACE_CHAR is used for indentation.
        int glyphRunCount = 0;
        boolean withinGlyphRun = false;
        for (int i = 0; i < endIndex; ++i) {
            char ch = text.charAt(i);
            boolean prevWithinGlyphRun = withinGlyphRun;
            withinGlyphRun = ch == SBC_CASE_SPACE_CHAR || (ch != SPACE_CHAR && ch <= 0x7f);

            if (i > 0 && (!withinGlyphRun || (withinGlyphRun && !prevWithinGlyphRun))) {
                ++glyphRunCount;
            }
        }

        float kerning = (paint.getRenderWidth() - actualWidth) / glyphRunCount;

        float previousPosX = startX;
        withinGlyphRun = false;
        for (int i = 0, j = 0; i < endIndex; ++i, j += 2) {
            char ch = text.charAt(i);
            boolean prevWithinGlyphRun = withinGlyphRun;
            withinGlyphRun = ch == SBC_CASE_SPACE_CHAR || (ch != SPACE_CHAR && ch <= 0x7f);

            if (i > 0 && (!withinGlyphRun || (withinGlyphRun && !prevWithinGlyphRun))) {
                previousPosX += kerning;
            }

            positions[j] = previousPosX;
            positions[j + 1] = constantY;

            previousPosX += paint.measureText(text, i, i + 1);
        }
    }

}
