package com.duowan.mobile.ixiaoshuo.view.finder;

import android.view.KeyEvent;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.ui.FinderMenuGridView;
import com.duowan.mobile.ixiaoshuo.ui.ScrollLayout;
import com.duowan.mobile.ixiaoshuo.view.ViewBuilder;

public class FinderView extends ViewBuilder {
	private ScrollLayout mLotMainContent;

	public FinderView(MainActivity activity, OnShowListener onShowListener) {
		mShowListener = onShowListener;
		mViewId = R.id.lotBookFinder;
		mActivity = activity;
	}

	@Override
	protected void build() {
		mView = mActivity.getLayoutInflater().inflate(R.layout.finder, null);
	}

	@Override
	public void init() {
		mLotMainContent = (ScrollLayout) findViewById(R.id.lotBookFinderContent);

		FinderMenuGridView finderMenuView = (FinderMenuGridView) findViewById(R.id.finderMenuView);
		showView(finderMenuView.buildViewBuilder(FinderMenuGridView.MENU_NEWLY));
		finderMenuView.setFinderView(this);
	}

	public void showView(ViewBuilder builder) {
		mLotMainContent.showView(builder);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return mLotMainContent.onKeyDown(keyCode, event);
	}

}
