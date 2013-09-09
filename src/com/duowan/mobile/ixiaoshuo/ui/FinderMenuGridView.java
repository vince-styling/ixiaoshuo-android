package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.view.ViewBuilder;
import com.duowan.mobile.ixiaoshuo.view.finder.*;

public class FinderMenuGridView extends SingleLineGridView {
	public FinderMenuGridView(Context context) {
		super(context);
	}

	public FinderMenuGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private String mBookType;

	public static final int MENU_NEWLY			= 10;
	public static final int MENU_HOTTEST		= 20;
	public static final int MENU_UPDATES        = 30;
	public static final int MENU_CATEGORIES		= 40;

	@Override
	protected void init() {
		mGridItems = new SparseArray<GridItem>(4);

		mGridItems.put(MENU_NEWLY, new GridItem(R.drawable.finder_new_on, R.drawable.finder_new_off, new ClickEvent() {
			public void onClick() {
				mFinderView.showView(buildViewBuilder(MENU_NEWLY));
			}
		}));

		mGridItems.put(MENU_HOTTEST, new GridItem(R.drawable.finder_hot_on, R.drawable.finder_hot_off, new ClickEvent() {
			public void onClick() {
				mFinderView.showView(buildViewBuilder(MENU_HOTTEST));
			}
		}));

		mGridItems.put(MENU_UPDATES, new GridItem(R.drawable.finder_finished_on, R.drawable.finder_finished_off, new ClickEvent() {
			public void onClick() {
				mFinderView.showView(buildViewBuilder(MENU_UPDATES));
			}
		}));

		mGridItems.put(MENU_CATEGORIES, new GridItem(R.drawable.finder_categories_on, R.drawable.finder_categories_off, new ClickEvent() {
			public void onClick() {
				mFinderView.showView(buildViewBuilder(MENU_CATEGORIES));
			}
		}));

		mPaddingTop = getResources().getDimensionPixelSize(R.dimen.finderMenuPadding);
	}

	public ViewBuilder buildViewBuilder(int menuId) {
		switch (menuId) {
			case MENU_NEWLY:
				return new FinderNewlyBookListView(mBookType, getActivity(), new ViewBuilder.OnShowListener() {
					@Override
					public void onShow() {
						selectItem(MENU_NEWLY);
					}
				});

			case MENU_HOTTEST:
				return new FinderHottestBookListView(mBookType, getActivity(), new ViewBuilder.OnShowListener() {
					@Override
					public void onShow() {
						selectItem(MENU_HOTTEST);
					}
				});

			case MENU_UPDATES:
				return new FinderUpdatesBookListView(mBookType, getActivity(), new ViewBuilder.OnShowListener() {
					@Override
					public void onShow() {
						selectItem(MENU_UPDATES);
					}
				});

			case MENU_CATEGORIES:
				return new FinderCategoriesView(mBookType, getActivity(), new ViewBuilder.OnShowListener() {
					@Override
					public void onShow() {
						selectItem(MENU_CATEGORIES);
					}
				});
		}
		return null;
	}

	private FinderView mFinderView;
	public void setFinderView(FinderView finderView) {
		mFinderView = finderView;
	}

	public void setBookType(String bookType) {
		mBookType = bookType;
	}

}
