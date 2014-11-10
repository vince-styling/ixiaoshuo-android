package com.vincestyling.ixiaoshuo.view.reader;

import android.view.View;
import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.reader.ReaderActivity;

public class FontSetting extends SettingBase implements View.OnClickListener {
    private TextView mTxvCurrentFont;
    private View mBtnFontDecrease, mBtnFontIncrease;

    public FontSetting(ReaderActivity activity) {
        super(activity, R.id.lotFontSetting);
    }

    @Override
    protected void build() {
        inflate(R.layout.reading_board_font_setting);

        mTxvCurrentFont = (TextView) mView.findViewById(R.id.txvCurrentFont);
        setTextSizePrompt();

        mBtnFontDecrease = mView.findViewById(R.id.btnFontDecrease);
        mBtnFontDecrease.setOnClickListener(this);

        mBtnFontIncrease = mView.findViewById(R.id.btnFontIncrease);
        mBtnFontIncrease.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnFontDecrease) {
            mReaderActivity.decreaseTextSize();
        } else if (v == mBtnFontIncrease) {
            mReaderActivity.increaseTextSize();
        }
        setTextSizePrompt();
    }

    private void setTextSizePrompt() {
        mTxvCurrentFont.setText(String.format(mReaderActivity.getString(
                R.string.reading_board_setting_font_prompt),mReaderActivity.getPreferences().getTextSize()));
    }
}
