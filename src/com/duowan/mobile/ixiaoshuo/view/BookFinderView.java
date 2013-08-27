package com.duowan.mobile.ixiaoshuo.view;

import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;

public class BookFinderView extends ViewBuilder {

	public BookFinderView(MainActivity activity) {
		this.mViewId = R.id.lotBookFinder;
		this.mActivity = activity;
	}

	@Override
	protected void build() {
		mView = mActivity.getLayoutInflater().inflate(R.layout.book_finder, null);
	}

	@Override
	public void init() {}

}
