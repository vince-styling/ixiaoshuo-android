package com.duowan.mobile.ixiaoshuo.view;

import android.view.ViewGroup;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.event.Notifier;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;

public class BookshelfView extends ViewBuilder {
	private BookshelfContentView mBookshelfView;

	public BookshelfView(MainActivity activity) {
		this.mViewId = R.id.lotBookshelf;
		this.mActivity = activity;
	}

	protected void build() {
		mView = (ViewGroup) mActivity.getLayoutInflater().inflate(R.layout.book_shelf, null);

		mBookshelfView = new BookshelfContentView();
		mBookshelfView.init(findViewById(R.id.lsvBookShelf), findViewById(R.id.lotWithoutBooks));

		mActivity.getReaderApplication().getMainHandler().putNotifier(Notifier.NOTIFIER_BOOKSHELF_REFRESH, new Notifier() {
			public void onNotified() {
				mBookshelfView.reloadBookShelf();
			}
		});
	}

}
