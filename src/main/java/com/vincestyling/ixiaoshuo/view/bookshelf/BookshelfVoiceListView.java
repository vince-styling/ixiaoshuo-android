package com.vincestyling.ixiaoshuo.view.bookshelf;

import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.db.AppDAO;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.reader.MainActivity;

public class BookshelfVoiceListView extends BookshelfBaseListView {

	public BookshelfVoiceListView(MainActivity activity, OnShowListener onShowListener) {
		super(activity, R.id.lotBookShelfVoice, onShowListener);
	}

	@Override
	public void loadData() {
		mBookList = AppDAO.get().getBookListOnBookShelf(Book.TYPE_VOICE);
	}

	@Override
	protected void setFinderTip(TextView txvFinderTip) {
		txvFinderTip.setText(R.string.without_book_finder_tip3);
	}

}
