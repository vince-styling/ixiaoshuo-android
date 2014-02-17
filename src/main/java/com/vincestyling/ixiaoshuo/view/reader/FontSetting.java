package com.vincestyling.ixiaoshuo.view.reader;

import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.reader.ReaderActivity;

public class FontSetting extends ReaderViewBuilder {

	public FontSetting(ReaderActivity activity, OnShowListener showListener) {
		super(activity, R.id.lotFontSetting, showListener);
	}

	@Override
	protected void build() {
		mView = getActivity().getLayoutInflater().inflate(R.layout.reading_board_font_setting, null);
	}

	@Override
	public void init() {
		TextView mTxvCurrentFont = (TextView) findViewById(R.id.txvCurrentFont);
		mTxvCurrentFont.setText("当前 38 号字");
	}
}
