package com.duowan.mobile.ixiaoshuo.reader;

import android.os.Bundle;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.utils.ViewUtil;

public class ReaderActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtil.setFullScreen(this);
		setContentView(R.layout.reading_board);

		int bid = Integer.parseInt(getIntent().getAction());
	}

}
