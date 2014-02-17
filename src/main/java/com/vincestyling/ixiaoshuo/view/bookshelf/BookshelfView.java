package com.vincestyling.ixiaoshuo.view.bookshelf;

import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.event.Notifier;
import com.vincestyling.ixiaoshuo.reader.MainActivity;
import com.vincestyling.ixiaoshuo.ui.ScrollLayout;
import com.vincestyling.ixiaoshuo.view.ViewBuilder;

public class BookshelfView extends ViewBuilder {
	private ScrollLayout mLotMainContent;
	Button mBtnTextBookshelf, mBtnVoiceBookshelf;

	public BookshelfView(MainActivity activity, OnShowListener onShowListener) {
		mShowListener = onShowListener;
		mViewId = R.id.lotBookshelf;
		setActivity(activity);
	}

	@Override
	protected void build() {
		mView = getActivity().getLayoutInflater().inflate(R.layout.book_shelf, null);
	}

	@Override
	public void init() {
		mLotMainContent = (ScrollLayout) findViewById(R.id.lotBookShelfContent);

		mBtnTextBookshelf = (Button) findViewById(R.id.btnTextBookshelf);
		mBtnVoiceBookshelf = (Button) findViewById(R.id.btnVoiceBookshelf);

		mBtnTextBookshelf.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLotMainContent.showView(new BookshelfTextListView(getActivity(), new OnShowListener() {
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
				mLotMainContent.showView(new BookshelfVoiceListView(getActivity(), new OnShowListener() {
					@Override
					public void onShow() {
						highlightBtn(mBtnVoiceBookshelf);
					}
				}));
				highlightBtn(mBtnVoiceBookshelf);
			}
		});

		mBtnTextBookshelf.performClick();

		getActivity().getReaderApplication().getMainHandler().putNotifier(Notifier.NOTIFIER_BOOKSHELF_REFRESH, new Notifier() {
			public void onNotified() {
				mLotMainContent.resumeView();
			}
		});
	}

	private void highlightBtn(View btnView) {
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

	@Override
	public MainActivity getActivity() {
		return (MainActivity) super.getActivity();
	}

}
