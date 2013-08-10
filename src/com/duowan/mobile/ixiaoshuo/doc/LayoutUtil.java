package com.duowan.mobile.ixiaoshuo.doc;

import android.graphics.Paint;

public class LayoutUtil {
	private final static char SPACE_CHAR = ' ';	// \u0020
	private final static char SBC_CASE_SPACE_CHAR = '　';	// \u3000
	
	public static boolean isASCII (char ch) {
		return ch <= 0x7f && ch != 0x20;
	}
	
	public static void trimSpaces (StringBuilder sb) {
		int len = sb.length();
		if(len > 0) {
			int i = -1;
			// trim left
			while (++i < len && sb.charAt(i) == 0x20);
			
			if(i > 0)
				sb.delete(0, i);
			len = sb.length();
			
			// trim right
			if(len > 0) {
				i = len;
				while (--i >= 0 && sb.charAt(i) == 0x20);
				++i;
				if(i < len)
					sb.delete(i, len);
			}
		}
	}
	
	public static boolean isAlphanumeric (char ch) {
		return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9');
	}
	
	public static boolean isSBCPunctuation (char ch) {
		switch (ch) {
			case '！':
			case '？':
			case '“':	// opening double quote
			case '”':	// closing double quote
			case '：':
			case '‘':	// opening single quote
			case '’':	// closing single quote
			case '，':
			case '。':
			case '；':
			case '…':
			case '、':
				return true;
		}
		return false;
	}
	
	public static boolean isPunctuation (char ch) {
		switch (ch) {
			case '!':
			case '！':
			case '?':
			case '？':
			case '"':
			case '“':	// opening double quote
			case '”':	// closing double quote
			case ':':
			case '：':
			case '\'':
			case '‘':	// opening single quote
			case '’':	// closing single quote
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
	
	public static void layoutTextJustified (CharSequence text, int maxWidth, Paint paint, float startX, float constantY, float[] positions) {
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
			
			if(i > 0 && (!withinGlyphRun || (withinGlyphRun && !prevWithinGlyphRun))) {
				++glyphRunCount;
			}
		}
		
		float kerning = (maxWidth - actualWidth) / glyphRunCount;
		
		float previousPosX = startX;
		withinGlyphRun = false;
		for (int i = 0, j = 0; i < endIndex; ++i, j+=2) {
			char ch = text.charAt(i);
			boolean prevWithinGlyphRun = withinGlyphRun;
			withinGlyphRun = ch == SBC_CASE_SPACE_CHAR || (ch != SPACE_CHAR && ch <= 0x7f);
			
			if(i > 0 && (!withinGlyphRun || (withinGlyphRun && !prevWithinGlyphRun))) {
				previousPosX += kerning;
			}
			
			positions[j] = previousPosX;
			positions[j+1] = constantY;
			
			previousPosX += paint.measureText(text, i, i + 1);
		}
	}

}
