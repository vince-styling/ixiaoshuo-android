package com.duowan.mobile.ixiaoshuo.view;

import android.view.View;
import android.widget.Button;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.event.Notifier;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.ui.ScrollLayout;

public class BookshelfView extends ViewBuilder {
	private ScrollLayout mLotBookShelfContent;

	public BookshelfView(MainActivity activity) {
		this.mViewId = R.id.lotBookshelf;
		this.mActivity = activity;
	}

	@Override
	protected void build() {
		mView = mActivity.getLayoutInflater().inflate(R.layout.book_shelf, null);
	}

	@Override
	public void init() {
		mLotBookShelfContent = (ScrollLayout) findViewById(R.id.lotBookShelfContent);

		Button btnTextBookshelf = (Button) findViewById(R.id.btnTextBookshelf);
		btnTextBookshelf.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLotBookShelfContent.showView(new BookshelfContentView(mActivity));
			}
		});

		btnTextBookshelf.performClick();

		mActivity.getReaderApplication().getMainHandler().putNotifier(Notifier.NOTIFIER_BOOKSHELF_REFRESH, new Notifier() {
			public void onNotified() {
				mLotBookShelfContent.resumeView(new BookshelfContentView(mActivity));
			}
		});
	}

}
