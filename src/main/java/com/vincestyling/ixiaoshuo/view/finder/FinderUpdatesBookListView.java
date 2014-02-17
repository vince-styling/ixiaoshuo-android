package com.vincestyling.ixiaoshuo.view.finder;

import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.reader.MainActivity;

public class FinderUpdatesBookListView extends FinderBaseListView {

	public FinderUpdatesBookListView(int bookType, MainActivity activity, OnShowListener onShowListener) {
		super(bookType, activity, R.id.lsvFinderUpdatesBooks, onShowListener);
	}

	@Override
	protected void loadData() {
		Netroid.getBookListByUpdateStatus(Book.STATUS_FINISHED, mPageNo, getListener());
	}

	@Override
	protected void setBookTips(TextView txvBookTips, Book book) {
		txvBookTips.setText(book.getCatName());
	}

}
