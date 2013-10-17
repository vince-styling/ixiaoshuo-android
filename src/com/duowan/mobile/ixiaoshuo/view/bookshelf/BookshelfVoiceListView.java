package com.duowan.mobile.ixiaoshuo.view.bookshelf;

import android.widget.TextView;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.db.AppDAO;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;

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
