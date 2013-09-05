package com.duowan.mobile.ixiaoshuo.view.finder;

import android.view.KeyEvent;
import android.view.View;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.ui.ScrollLayout;
import com.duowan.mobile.ixiaoshuo.view.ViewBuilder;

public class FinderCategoriesView extends ViewBuilder {

	public FinderCategoriesView(MainActivity activity, OnShowListener onShowListener) {
		mViewId = R.id.lotFinderCategoriesContent;
		mShowListener = onShowListener;
		mActivity = activity;
	}

	@Override
	protected void build() {
		mView = mActivity.getLayoutInflater().inflate(R.layout.finder_book_categories, null);
	}

	@Override
	public void resume() {
		showView(new FinderCategoryListView(getActivity(), this));
		super.resume();
	}

	public void showView(ViewBuilder builder) {
		getScrollLayout().showView(builder);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return mView.onKeyDown(keyCode, event);
	}

	private ScrollLayout getScrollLayout() {
		return (ScrollLayout) mView;
	}

}
