package com.vincestyling.ixiaoshuo.view.finder;

import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.NetService;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.reader.MainActivity;
import com.vincestyling.ixiaoshuo.utils.PaginationList;
import com.vincestyling.ixiaoshuo.utils.ViewUtil;

public class FinderCategoryBookListView extends FinderUpdatesBookListView {
	private int mCatId;
	private int mBookType;

	public FinderCategoryBookListView(int catId, int bookType, MainActivity activity) {
		super(bookType, activity, null);

		// crash(NullPointerException) might be occur during ScrollLayout.showView().getTag() method
		mViewId = ViewUtil.generateViewId(R.id.lsvFinderCategoryBooks, ViewUtil.VIEWID_INCREASE_FACTOR_SML, catId);
		mBookType = bookType;
		mCatId = catId;
	}

	@Override
	protected PaginationList<Book> loadData() {
		return NetService.get().getBookListByCategory(mBookType, mCatId, mPageNo, PAGE_ITEM_COUNT);
	}

	@Override
	public boolean canShowBack() {
		return false;
	}

}
