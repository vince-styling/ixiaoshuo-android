package com.vincestyling.ixiaoshuo.view.bookshelf;

import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.db.AppDAO;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.reader.MainActivity;

public class BookshelfTextListView extends BookshelfBaseListView {

	public BookshelfTextListView(MainActivity activity, OnShowListener onShowListener) {
		super(activity, R.id.lotBookShelfText, onShowListener);
	}

	@Override
	public void loadData() {
		mBookList = AppDAO.get().getBookListOnBookShelf(Book.TYPE_TEXT);
	}

}
