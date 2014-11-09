package com.vincestyling.ixiaoshuo.view.reader;

import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.reader.ReaderActivity;

public class FontSetting extends SettingBase {
    public FontSetting(ReaderActivity activity) {
        super(activity, R.id.lotFontSetting);
    }

    @Override
    protected void build() {
        inflate(R.layout.reading_board_font_setting);
        TextView mTxvCurrentFont = (TextView) mView.findViewById(R.id.txvCurrentFont);
        mTxvCurrentFont.setText("当前 38 号字");
    }
}
