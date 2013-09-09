package com.duowan.mobile.ixiaoshuo.view.finder;

import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.utils.PaginationList;
import com.duowan.mobile.ixiaoshuo.utils.ViewUtil;

public class FinderCategoryBookListView extends FinderUpdatesBookListView {
	private int mCatId;
	private String mBookType;

	public FinderCategoryBookListView(int catId, String bookType, MainActivity activity) {
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
