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

    private int mTextSize;
    private static final String TEXT_SIZE = "textSize";
    private static final int TEXT_SIZE_ADJUST_RANGE = 2;
    private int mMaxTextSize;
    private int mMinTextSize;

    private int mBrightness;
    private static final String BRIGHTNESS = "brightness";
    public static final int MIN_BRIGHT = 5;

    private boolean mIsDarkMode;
    private static final String DARK_MODE = "isDarkMode";

    private ColorScheme[] mColorSchemes;
    private ColorScheme mDarkColorScheme;

    private boolean mIsPortMode;
    private static final String PORT_MODE = "isPortMode";

    public ReadingPreferences(Context ctx) {
        mCtx = ctx;

        SharedPreferences sharedPreferences = getPreferences();

        mTextSize = sharedPreferences.getInt(TEXT_SIZE, 0);
        if (mTextSize == 0) {
            mTextSize = mCtx.getResources().getDimensionPixelSize(R.dimen.reading_board_default_text_size);
        }
        mMinTextSize = mCtx.getResources().getDimensionPixelSize(R.dimen.reading_board_min_text_size);
        mMaxTextSize = mCtx.getResources().getDimensionPixelSize(R.dimen.reading_board_max_text_size);

        mColorSchemes = new ColorScheme[]{
                new ColorScheme(mCtx.getResources().getString(R.string.reading_menu_scheme_green1), 0xffdfeed6, 0xff1e2b16, true),
                new ColorScheme(mCtx.getResources().getString(R.string.reading_menu_scheme_yellow), R.drawable.reading_bg_yellow, 0xff543927, false),
                new ColorScheme(mCtx.getResources().getString(R.string.reading_menu_scheme_green), R.drawable.reading_bg_green, 0xff546b56, false),
                new ColorScheme(mCtx.getResources().getString(R.string.reading_menu_scheme_blue), R.drawable.reading_bg_blue, 0xff3e506a, false),
        };
        int defaultColorSchemeIndex = 1;
        mColorSchemeIndex = sharedPreferences.getInt(COLOR_SCHEME_INDEX, -1);
        if (mColorSchemeIndex == -1) mColorSchemeIndex = defaultColorSchemeIndex;

        mDarkColorScheme = new ColorScheme("", 0xff000000, 0xff444444, true);
        mIsDarkMode = sharedPreferences.getBoolean(DARK_MODE, false);
        mIsPortMode = sharedPreferences.getBoolean(PORT_MODE, true);

        mBrightness = sharedPreferences.getInt(BRIGHTNESS, -1);
    }

    public SharedPreferences getPreferences() {
        return mCtx.getSharedPreferences(Const.SETTING, Context.MODE_PRIVATE);
    }

    public boolean isDarkMode() {
        return mIsDarkMode;
    }

    public int getBrightness() {
        return mBrightness;
    }

    public ColorScheme getColorScheme() {
        return mIsDarkMode ? mDarkColorScheme : mColorSchemes[mColorSchemeIndex];
    }

    public ColorScheme[] getColorSchemes() {
        return mColorSchemes;
    }

    public int getColorSchemeIndex() {
        return mColorSchemeIndex;
    }

    public boolean isPortMode() {
        return mIsPortMode;
    }

    public void setColorSchemeIndex(int colorSchemeIndex) {
        putInt(COLOR_SCHEME_INDEX, colorSchemeIndex);
        mColorSchemeIndex = colorSchemeIndex;
        setIsDarkMode(false);
    }

    public void setBrightness(int brightness) {
        putInt(BRIGHTNESS, brightness);
        mBrightness = brightness;
    }

    public void setIsDarkMode(boolean isDarkMode) {
        putBoolean(DARK_MODE, isDarkMode);
        mIsDarkMode = isDarkMode;
    }

    public boolean increaseTextSize() {
        if (mTextSize + TEXT_SIZE_ADJUST_RANGE <= mMaxTextSize) {
            mTextSize += TEXT_SIZE_ADJUST_RANGE;
            putInt(TEXT_SIZE, mTextSize);
            return true;
        }
        return false;
    }

    public boolean decreaseTextSize() {
        if (mTextSize - TEXT_SIZE_ADJUST_RANGE >= mMinTextSize) {
            mTextSize -= TEXT_SIZE_ADJUST_RANGE;
            putInt(TEXT_SIZE, mTextSize);
            return true;
        }
        return false;
    }

    public void setIsPortMode(boolean isPortMode) {
        putBoolean(PORT_MODE, isPortMode);
        mIsPortMode = isPortMode;
    }

    public int getTextSize() {
        return mTextSize;
    }

    private void putInt(String key, int value) {
        SharedPreferences sharedPreferences = getPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private void putBoolean(String key, boolean value) {
        SharedPreferences sharedPreferences = getPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

}
