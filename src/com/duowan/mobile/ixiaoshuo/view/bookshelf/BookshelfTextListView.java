package com.duowan.mobile.ixiaoshuo.view.bookshelf;

import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.db.AppDAO;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;

public class BookshelfTextListView extends BookshelfBaseListView {

	public BookshelfTextListView(MainActivity activity, OnShowListener onShowListener) {
		super(activity, R.id.lotBookShelfText, onShowListener);
	}

	@Override
	public void loadData() {
		mBookList = AppDAO.get().getBookListOnBookShelf(Book.TYPE_TEXT);
	}

}
