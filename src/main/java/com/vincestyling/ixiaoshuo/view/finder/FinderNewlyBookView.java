package com.vincestyling.ixiaoshuo.view.finder;

import android.widget.TextView;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.utils.StringUtil;

public class FinderNewlyBookView extends FinderBaseListView {

	@Override
	protected void loadData() {
		Netroid.getNewlyBookList(mPageNo, getListener());
	}

	@Override
	protected void setBookTips(TextView txvBookTips, Book book) {
		txvBookTips.setText(StringUtil.getDiffWithNow(book.getLastUpdateTime()));
	}
}
