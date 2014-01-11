package com.vincestyling.ixiaoshuo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.pojo.ColorScheme;
import com.vincestyling.ixiaoshuo.pojo.Const;

public class ReadingPreferences {
	private Context mCtx;

	private int mColorSchemeIndex;
	private static final String COLOR_SCHEME_INDEX = "colorSchemeIndex";

	private int mBrightness;
	private static final String BRIGHTNESS = "brightness";
	public static final int MIN_BRIGHT = 5;                 /** 防止灭屏的最小亮度 */

	private boolean mIsDarkMode;
	private static final String DARK_MODE = "isDarkMode";

	private ColorScheme[] mColorSchemes;
	private ColorScheme mDarkColorScheme;

	public ReadingPreferences(Context ctx) {
		mCtx = ctx;

		mColorSchemes = new ColorScheme[] {
				new ColorScheme(R.drawable.reading_bg_yellow, 0xff543927, false),
		};
//		mDarkColorScheme = new ColorScheme(R.drawable.reading_bg_dark, 0xff818e90);
//		mDarkColorScheme = new ColorScheme(0xff2C2C2C, 0xffa6a6a6, true);
		mDarkColorScheme = new ColorScheme(0xff000000, 0xff444444, true);

		SharedPreferences sharedPreferences = getPreferences();

		mColorSchemeIndex = sharedPreferences.getInt(COLOR_SCHEME_INDEX, 0);

		mBrightness = sharedPreferences.getInt(BRIGHTNESS, -1);

		mIsDarkMode = sharedPreferences.getBoolean(DARK_MODE, false);
	}

	public SharedPreferences getPreferences() {
		return mCtx.getSharedPreferences(Const.SETTING, Context.MODE_PRIVATE);
	}

	public boolean isDarkMode() {
		return mIsDarkMode;
	}

	public ColorScheme getColorScheme() {
		return mIsDarkMode ? mDarkColorScheme : mColorSchemes[mColorSchemeIndex];
	}

	public int getBrightness() {
		return mBrightness;
	}

	public void setColorSchemeIndex(int colorSchemeIndex) {
		putInt(COLOR_SCHEME_INDEX, colorSchemeIndex);
		mColorSchemeIndex = colorSchemeIndex;
	}

	public void setBrightness(int brightness) {
		putInt(BRIGHTNESS, brightness);
		mBrightness = brightness;
	}

	public void setIsDarkMode(boolean isDarkMode) {
		putBoolean(DARK_MODE, isDarkMode);
		mIsDarkMode = isDarkMode;
	}

	private void putInt(String key, int value) {
		SharedPreferences sharedPreferences = getPreferences();
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	private void putBoolean(String key, boolean value) {
		SharedPreferences sharedPreferences = getPreferences();
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

}
