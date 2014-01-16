package com.vincestyling.ixiaoshuo.view.finder;

import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.reader.MainActivity;
import com.vincestyling.ixiaoshuo.utils.StringUtil;

public class FinderNewlyBookListView extends FinderBaseListView {

	public FinderNewlyBookListView(int bookType, MainActivity activity, OnShowListener onShowListener) {
		super(bookType, activity, R.id.lsvFinderNewlyBooks, onShowListener);
	}

	@Override
	protected void loadData() {
		Netroid.getNewlyBookList(mPageNo, getListener());
	}

	@Override
	protected void setBookTips(TextView txvBookTips, Book book) {
		txvBookTips.setText(StringUtil.getDiffWithNow(book.getLastUpdateTime()));
	}
}
