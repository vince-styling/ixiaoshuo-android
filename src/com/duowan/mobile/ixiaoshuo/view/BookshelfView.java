package com.duowan.mobile.ixiaoshuo.view;

import android.graphics.Color;
import android.view.KeyEvent;
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

		final Button btnTextBookshelf = (Button) findViewById(R.id.btnTextBookshelf);
		final Button btnVoiceBookshelf = (Button) findViewById(R.id.btnVoiceBookshelf);
		final Button btnLocalBookshelf = (Button) findViewById(R.id.btnLocalBookshelf);

		btnTextBookshelf.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLotBookShelfContent.showView(new BookshelfTextListView(mActivity, new OnShowListener() {
					@Override
					public void onShow() {
						btnTextBookshelf.performClick();
					}
				}));
				turnOnButton(btnTextBookshelf);
				turnOffButton(btnVoiceBookshelf);
				turnOffButton(btnLocalBookshelf);
			}
		});

		btnVoiceBookshelf.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLotBookShelfContent.showView(new BookshelfVoiceListView(mActivity, new OnShowListener() {
					@Override
					public void onShow() {
						btnVoiceBookshelf.performClick();
					}
				}));
				turnOnButton(btnVoiceBookshelf);
				turnOffButton(btnTextBookshelf);
				turnOffButton(btnLocalBookshelf);
			}
		});

		btnLocalBookshelf.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLotBookShelfContent.showView(new BookshelfLocalListView(mActivity, new OnShowListener() {
					@Override
					public void onShow() {
						btnLocalBookshelf.performClick();
					}
				}));
				turnOnButton(btnLocalBookshelf);
				turnOffButton(btnTextBookshelf);
				turnOffButton(btnVoiceBookshelf);
			}
		});

		btnTextBookshelf.performClick();

		mActivity.getReaderApplication().getMainHandler().putNotifier(Notifier.NOTIFIER_BOOKSHELF_REFRESH, new Notifier() {
			public void onNotified() {
				mLotBookShelfContent.resumeView();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return mLotBookShelfContent.onKeyDown(keyCode, event);
	}

	private void turnOnButton(Button btnView) {
		btnView.setBackgroundResource(R.drawable.title_item_selected);
		btnView.setTextColor(Color.parseColor("#fefffc"));
	}

	private void turnOffButton(Button btnView) {
		btnView.setBackgroundColor(Color.TRANSPARENT);
		btnView.setTextColor(Color.parseColor("#b1d596"));
	}

}
