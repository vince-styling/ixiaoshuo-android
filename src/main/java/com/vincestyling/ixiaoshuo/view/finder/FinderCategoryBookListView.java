package com.vincestyling.ixiaoshuo.view.finder;

import com.vincestyling.ixiaoshuo.net.Netroid;

public class FinderCategoryBookListView extends FinderHottestBookView {
	private int mCatId;

	@Override
	protected void loadData() {
		Netroid.getBookListByCategory(mCatId, mPageNo, getListener());
	}

}
