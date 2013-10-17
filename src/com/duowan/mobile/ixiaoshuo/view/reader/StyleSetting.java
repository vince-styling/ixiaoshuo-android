package com.duowan.mobile.ixiaoshuo.view.reader;

import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.reader.ReaderActivity;

public class StyleSetting extends ReaderViewBuilder {

	public StyleSetting(ReaderActivity activity, OnShowListener showListener) {
		super(activity, R.id.lotStyleSetting, showListener);
	}

	@Override
	protected void build() {
		mView = getActivity().getLayoutInflater().inflate(R.layout.reading_board_style_setting, null);
	}

}
