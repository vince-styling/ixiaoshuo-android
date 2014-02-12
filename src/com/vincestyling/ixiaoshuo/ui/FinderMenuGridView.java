package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.util.AttributeSet;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.event.OnGridItemClickListener;
import com.vincestyling.ixiaoshuo.reader.MainActivity;
import com.vincestyling.ixiaoshuo.view.ViewBuilder;
import com.vincestyling.ixiaoshuo.view.finder.*;

public class FinderMenuGridView extends GridView {
	private int mBookType;

	public static final int MENU_NEWLY = 10;
	public static final int MENU_HOTTEST = 20;
	public static final int MENU_UPDATES = 30;
	public static final int MENU_CATEGORIES = 40;

	public FinderMenuGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void initGrid() {
		putItem(new GridItem(R.drawable.finder_new_on, R.drawable.finder_new_off, new OnGridItemClickListener(MENU_NEWLY) {
			public void onGridItemClick() {
				mFinderView.showView(buildViewBuilder(getGridItemId()));
			}
		}));

		putItem(new GridItem(R.drawable.finder_hot_on, R.drawable.finder_hot_off, new OnGridItemClickListener(MENU_HOTTEST) {
			public void onGridItemClick() {
				mFinderView.showView(buildViewBuilder(getGridItemId()));
			}
		}));

		putItem(new GridItem(R.drawable.finder_finished_on, R.drawable.finder_finished_off, new OnGridItemClickListener(MENU_UPDATES) {
			public void onGridItemClick() {
				mFinderView.showView(buildViewBuilder(getGridItemId()));
			}
		}));

		putItem(new GridItem(R.drawable.finder_categories_on, R.drawable.finder_categories_off, new OnGridItemClickListener(MENU_CATEGORIES) {
			public void onGridItemClick() {
				mFinderView.showView(buildViewBuilder(getGridItemId()));
			}
		}));
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

	protected MainActivity getActivity() {
		return (MainActivity) getContext();
	}

	public void setBookType(int bookType) {
		mBookType = bookType;
	}

}
