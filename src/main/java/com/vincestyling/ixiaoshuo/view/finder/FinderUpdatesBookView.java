package com.vincestyling.ixiaoshuo.view.finder;

import android.widget.TextView;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Book;

public class FinderUpdatesBookView extends FinderBaseListView {

	@Override
	protected void loadData() {
		Netroid.getBookListByUpdateStatus(Book.STATUS_FINISHED, mPageNum, getListener());
	}

	@Override
	protected void setBookTips(TextView txvBookTips, Book book) {
		txvBookTips.setText(book.getCatName());
	}

}
