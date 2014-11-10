package com.vincestyling.ixiaoshuo.view.reader;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.reader.ReaderActivity;
import com.vincestyling.ixiaoshuo.utils.ReadingPreferences;
import com.vincestyling.ixiaoshuo.utils.SysUtil;

public class BrightnessSetting extends SettingBase implements SeekBar.OnSeekBarChangeListener, View.OnTouchListener {
    private Button mBtnLight, mBtnDark;
    private SeekBar mSeekBright;

    public BrightnessSetting(ReaderActivity activity) {
        super(activity, R.id.lotBrightnessSetting);
    }

    @Override
    protected void build() {
        inflate(R.layout.reading_board_brightness_setting);

        mSeekBright = (SeekBar) mView.findViewById(R.id.seekBright);
        mSeekBright.setOnSeekBarChangeListener(this);

        mBtnLight = (Button) mView.findViewById(R.id.buttonLight);
        mBtnLight.setOnTouchListener(this);

        mBtnDark = (Button) mView.findViewById(R.id.buttonDark);
        mBtnDark.setOnTouchListener(this);
    }

    @Override
    public void resume() {
        int savedBrightness = mReaderActivity.getPreferences().getBrightness();
        if (savedBrightness == -1) {
            // Not Changed Brightness
//            int brightness = ScreenInfo.getBrightness(getActivity());
            int brightness = 255;
            int progress = brightToSeekBarProgress(brightness);
            mSeekBright.setProgress(progress);
        } else {
            int progress = brightToSeekBarProgress(savedBrightness);
            mSeekBright.setProgress(progress);
        }

        if (mReaderActivity.getPreferences().isDarkMode()) {
            mBtnDark.setPressed(true);
            mBtnLight.setPressed(false);
        } else {
            mBtnDark.setPressed(false);
            mBtnLight.setPressed(true);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) return true;
        if (event.getAction() != MotionEvent.ACTION_UP) return false;

        if (view == mBtnDark) {
            if (mReaderActivity.getPreferences().isDarkMode()) return true;
            mReaderActivity.getPreferences().setIsDarkMode(true);
            mBtnDark.setPressed(true);
            mBtnLight.setPressed(false);
        } else {
            if (!mReaderActivity.getPreferences().isDarkMode()) return true;
            mReaderActivity.getPreferences().setIsDarkMode(false);
            mBtnLight.setPressed(true);
            mBtnDark.setPressed(false);
        }

        mReaderActivity.onChangeColorScheme();
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int brightness = seekBarProgressToBright(progress);
        SysUtil.setBrightness(mReaderActivity.getWindow(), brightness);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int brightness = SysUtil.getBrightness(mReaderActivity.getWindow());
        mReaderActivity.getPreferences().setBrightness(brightness);
    }

    private int seekBarProgressToBright(int progress) {
        // 为防止某些机器上亮度太低导致灭屏，对亮度加上常量MIN_BRIGHT
        return progress + ReadingPreferences.MIN_BRIGHT;
    }

    private int brightToSeekBarProgress(int bright) {
        if (bright >= ReadingPreferences.MIN_BRIGHT) {
            return bright - ReadingPreferences.MIN_BRIGHT;
        }
        return 0;
    }
}
