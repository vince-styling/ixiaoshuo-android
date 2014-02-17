package com.vincestyling.ixiaoshuo.view.bookshelf;

import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.db.AppDAO;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.reader.MainActivity;

public class BookshelfLocalListView extends BookshelfBaseListView {

	public BookshelfLocalListView(MainActivity activity, OnShowListener onShowListener) {
		super(activity, R.id.lotBookShelfLocal, onShowListener);
	}

	@Override
	public void loadData() {
		mBookList = AppDAO.get().getBookListOnBookShelf(Book.TYPE_LOCAL);
	}

}
