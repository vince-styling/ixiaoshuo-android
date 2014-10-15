package com.vincestyling.ixiaoshuo.view.reader;

import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.reader.ReaderActivity;

public class StyleSetting extends ReaderViewBuilder {

    public StyleSetting(ReaderActivity activity, OnShowListener showListener) {
        super(activity, R.id.lotStyleSetting, showListener);
    }

    @Override
    protected void build() {
        mView = getActivity().getLayoutInflater().inflate(R.layout.reading_board_style_setting, null);
    }

}
