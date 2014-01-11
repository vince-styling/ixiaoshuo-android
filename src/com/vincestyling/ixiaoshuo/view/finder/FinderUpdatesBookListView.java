package com.vincestyling.ixiaoshuo.view.finder;

import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.NetService;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.reader.MainActivity;
import com.vincestyling.ixiaoshuo.utils.PaginationList;

public class FinderUpdatesBookListView extends FinderBaseListView {

	public FinderUpdatesBookListView(int bookType, MainActivity activity, OnShowListener onShowListener) {
		super(bookType, activity, R.id.lsvFinderUpdatesBooks, onShowListener);
	}

	@Override
	protected PaginationList<Book> loadData() {
		return NetService.get().getBookListByUpdateStatus(Book.STATUS_FINISHED, mPageNo);
	}

	@Override
	protected void setBookTips(TextView txvBookTips, Book book) {
		txvBookTips.setText(book.getCatName());
	}

}
