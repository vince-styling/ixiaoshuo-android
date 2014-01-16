package com.vincestyling.ixiaoshuo.view.finder;

import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.reader.MainActivity;
import com.vincestyling.ixiaoshuo.utils.ViewUtil;

public class FinderCategoryBookListView extends FinderHottestBookListView {
	private int mCatId;

	public FinderCategoryBookListView(int catId, int bookType, MainActivity activity) {
		super(bookType, activity, null);

		// crash(NullPointerException) might be occur during ScrollLayout.showView().getTag() method
		mViewId = ViewUtil.generateViewId(R.id.lsvFinderCategoryBooks, ViewUtil.VIEWID_INCREASE_FACTOR_SML, catId);
		mBookType = bookType;
		mCatId = catId;
	}

	@Override
	protected void loadData() {
		Netroid.getBookListByCategory(mCatId, mPageNo, getListener());
	}

	@Override
	public boolean canShowBack() {
		return false;
	}

}
