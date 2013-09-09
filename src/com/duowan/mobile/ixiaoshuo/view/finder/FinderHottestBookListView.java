package com.duowan.mobile.ixiaoshuo.view.finder;

import android.widget.TextView;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.utils.PaginationList;

public class FinderHottestBookListView extends FinderBaseListView {

	public FinderHottestBookListView(String bookType, MainActivity activity, OnShowListener onShowListener) {
		super(bookType, activity, R.id.lsvFinderHottestBooks, onShowListener);
	}

	@Override
	protected void build() {
		mView = mActivity.getLayoutInflater().inflate(R.layout.finder_book_listview, null);
		mView.setId(mViewId);
	}

	@Override
	protected PaginationList<Book> loadData() {
		return NetService.get().getHottestBookList(mBookType, mPageNo, PAGE_ITEM_COUNT);
	}

	@Override
	protected void setBookTips(TextView txvBookTips, Book book) {
		txvBookTips.setText(book.getReaderCount() + "人看过");
	}

}
