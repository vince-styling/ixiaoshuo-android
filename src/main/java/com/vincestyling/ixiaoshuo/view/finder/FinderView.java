package com.vincestyling.ixiaoshuo.view.finder;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.ui.FinderMenuGridView;
import com.vincestyling.ixiaoshuo.ui.ScrollLayout;
import com.vincestyling.ixiaoshuo.view.BaseFragment;
import com.vincestyling.ixiaoshuo.view.ViewBuilder;

public class FinderView extends BaseFragment {
	public static final int PAGER_INDEX = 1;

	private ScrollLayout mLotMainContent;
	private FinderMenuGridView mFinderMenuView;
	private Button mBtnTextsBook, mBtnVoicesBook;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.finder, container, false);

		mLotMainContent = (ScrollLayout) view.findViewById(R.id.lotBookFinderContent);

		mFinderMenuView = (FinderMenuGridView) view.findViewById(R.id.finderMenuView);
		mFinderMenuView.setFinderView(this);

		mBtnTextsBook = (Button) view.findViewById(R.id.btnTextsBook);
		mBtnVoicesBook = (Button) view.findViewById(R.id.btnVoicesBook);

		mBtnTextsBook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnClick(mBtnTextsBook);
			}
		});

		mBtnVoicesBook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnClick(mBtnVoicesBook);
			}
		});

		onBtnClick(mBtnTextsBook);

		return view;
	}

	private void onBtnClick(Button btnView) {
		highlightBtn(btnView);
		mLotMainContent.removeAllViews();
		showView(mFinderMenuView.buildViewBuilder(mFinderMenuView.getSelectedItemId()));
	}

	private void highlightBtn(Button btnView) {
		if (btnView == mBtnTextsBook) {
			mFinderMenuView.setBookType(Book.TYPE_TEXT);
			turnOnButton(mBtnTextsBook);
		} else {
			turnOffButton(mBtnTextsBook);
		}

		if (btnView == mBtnVoicesBook) {
			mFinderMenuView.setBookType(Book.TYPE_VOICE);
			turnOnButton(mBtnVoicesBook);
		} else {
			turnOffButton(mBtnVoicesBook);
		}
	}

	private void turnOnButton(Button btnView) {
		btnView.setBackgroundResource(R.drawable.title_item_selected);
		btnView.setTextColor(Color.parseColor("#fefffc"));
	}

	private void turnOffButton(Button btnView) {
		btnView.setBackgroundColor(Color.TRANSPARENT);
		btnView.setTextColor(Color.parseColor("#b1d596"));
	}

	public void showView(ViewBuilder builder) {
		mLotMainContent.showView(builder);
	}

}
