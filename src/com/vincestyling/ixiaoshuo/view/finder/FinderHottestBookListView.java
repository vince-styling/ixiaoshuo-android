package com.vincestyling.ixiaoshuo.view.finder;

import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.NetService;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.reader.MainActivity;
import com.vincestyling.ixiaoshuo.utils.PaginationList;

public class FinderHottestBookListView extends FinderBaseListView {

	public FinderHottestBookListView(int bookType, MainActivity activity, OnShowListener onShowListener) {
		super(bookType, activity, R.id.lsvFinderHottestBooks, onShowListener);
	}

	@Override
	protected PaginationList<Book> loadData() {
		return NetService.get().getHottestBookList(mPageNo);
	}

	@Override
	protected void setBookTips(TextView txvBookTips, Book book) {
		txvBookTips.setText(book.getReaderCount() + "人看过");
	}

}
