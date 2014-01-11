package com.vincestyling.ixiaoshuo.view.finder;

import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.NetService;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.reader.MainActivity;
import com.vincestyling.ixiaoshuo.utils.PaginationList;
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
	protected PaginationList<Book> loadData() {
		return NetService.get().getBookListByCategory(mCatId, mPageNo);
	}

	@Override
	public boolean canShowBack() {
		return false;
	}

}
