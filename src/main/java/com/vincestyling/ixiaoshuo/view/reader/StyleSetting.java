package com.vincestyling.ixiaoshuo.view.reader;

import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.reader.ReaderActivity;

public class StyleSetting extends SettingBase {
    public StyleSetting(ReaderActivity activity) {
        super(activity, R.id.lotStyleSetting);
    }

    @Override
    protected void build() {
        inflate(R.layout.reading_board_style_setting);
    }
}
