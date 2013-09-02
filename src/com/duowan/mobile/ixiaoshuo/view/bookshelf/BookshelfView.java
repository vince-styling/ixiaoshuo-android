package com.duowan.mobile.ixiaoshuo.view.bookshelf;

import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.event.Notifier;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.ui.ScrollLayout;
import com.duowan.mobile.ixiaoshuo.view.ViewBuilder;

public class BookshelfView extends ViewBuilder {
	private ScrollLayout mLotMainContent;
	Button mBtnTextBookshelf, mBtnVoiceBookshelf, mBtnLocalBookshelf;


	public BookshelfView(MainActivity activity, OnShowListener onShowListener) {
		mShowListener = onShowListener;
		mViewId = R.id.lotBookshelf;
		mActivity = activity;
	}

	@Override
	protected void build() {
		mView = mActivity.getLayoutInflater().inflate(R.layout.book_shelf, null);
	}

	@Override
	public void init() {
		mLotMainContent = (ScrollLayout) findViewById(R.id.lotBookShelfContent);

		mBtnTextBookshelf = (Button) findViewById(R.id.btnTextBookshelf);
		mBtnVoiceBookshelf = (Button) findViewById(R.id.btnVoiceBookshelf);
		mBtnLocalBookshelf = (Button) findViewById(R.id.btnLocalBookshelf);

		mBtnTextBookshelf.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLotMainContent.showView(new BookshelfTextListView(mActivity, new OnShowListener() {
					@Override
					public void onShow() {
						highlightBtn(mBtnTextBookshelf);
					}
				}));
				highlightBtn(mBtnTextBookshelf);
			}
		});

		mBtnVoiceBookshelf.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLotMainContent.showView(new BookshelfVoiceListView(mActivity, new OnShowListener() {
					@Override
					public void onShow() {
						highlightBtn(mBtnVoiceBookshelf);
					}
				}));
				highlightBtn(mBtnVoiceBookshelf);
			}
		});

		mBtnLocalBookshelf.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLotMainContent.showView(new BookshelfLocalListView(mActivity, new OnShowListener() {
					@Override
					public void onShow() {
						highlightBtn(mBtnLocalBookshelf);
					}
				}));
				highlightBtn(mBtnLocalBookshelf);
			}
		});

		mBtnTextBookshelf.performClick();

		mActivity.getReaderApplication().getMainHandler().putNotifier(Notifier.NOTIFIER_BOOKSHELF_REFRESH, new Notifier() {
			public void onNotified() {
				mLotMainContent.resumeView();
			}
		});
	}

	private void highlightBtn(Button btnView) {
		if (btnView == mBtnTextBookshelf) {
			turnOnButton(mBtnTextBookshelf);
		} else {
			turnOffButton(mBtnTextBookshelf);
		}

		if (btnView == mBtnVoiceBookshelf) {
			turnOnButton(mBtnVoiceBookshelf);
		} else {
			turnOffButton(mBtnVoiceBookshelf);
		}

		if (btnView == mBtnLocalBookshelf) {
			turnOnButton(mBtnLocalBookshelf);
		} else {
			turnOffButton(mBtnLocalBookshelf);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return mLotMainContent.onKeyDown(keyCode, event);
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
