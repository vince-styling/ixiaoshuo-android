package com.vincestyling.ixiaoshuo.view.finder;

import com.duowan.mobile.netroid.Listener;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.utils.PaginationList;

public class FinderCategoryBookListView extends FinderSimplyHottestBookView {
	private int mCatId;

	@Override
	protected void loadData(int pageNum, Listener<PaginationList<Book>> listener) {
		Netroid.getBookListByCategory(mCatId, pageNum, listener);
	}

}
