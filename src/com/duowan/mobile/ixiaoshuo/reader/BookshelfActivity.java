package com.duowan.mobile.ixiaoshuo.reader;

import android.app.Activity;
import android.os.Bundle;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.view.BookshelfEmulateView;

public class BookshelfActivity extends Activity {
	private BookshelfEmulateView mBookshelfView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_shelf_emulate_style);

		mBookshelfView = new BookshelfEmulateView(this, findViewById(R.id.lsvBookShelf));
		mBookshelfView.initBookShelf(Book.getStaticBookList());
	}

}
