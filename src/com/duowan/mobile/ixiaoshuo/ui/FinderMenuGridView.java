package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.view.finder.FinderNewlyBookListView;
import com.duowan.mobile.ixiaoshuo.view.finder.FinderView;
import com.duowan.mobile.ixiaoshuo.view.ViewBuilder;

public class FinderMenuGridView extends SingleLineGridView {
	public FinderMenuGridView(Context context) {
		super(context);
	}

	public FinderMenuGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public static final int MENU_NEWLY			= 10;
	public static final int MENU_HOTTEST		= 20;
	public static final int MENU_FINISHED		= 30;
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
				getActivity().showToastMsg("点击了热门菜单");
			}
		}));

		mGridItems.put(MENU_FINISHED, new GridItem(R.drawable.finder_finished_on, R.drawable.finder_finished_off, new ClickEvent() {
			public void onClick() {
				getActivity().showToastMsg("点击了全本菜单");
			}
		}));

		mGridItems.put(MENU_CATEGORIES, new GridItem(R.drawable.finder_categories_on, R.drawable.finder_categories_off, new ClickEvent() {
			public void onClick() {
				getActivity().showToastMsg("点击了分类菜单");
			}
		}));

		mPaddingTop = getResources().getDimensionPixelSize(R.dimen.finderMenuPadding);
	}

	public ViewBuilder buildViewBuilder(int menuId) {
		if (menuId == MENU_NEWLY) {
			return new FinderNewlyBookListView(getActivity(), new ViewBuilder.OnShowListener() {
				@Override
				public void onShow() {
					selectItem(MENU_NEWLY);
				}
			});
		}
		return null;
	}

	private FinderView mFinderView;
	public void setFinderView(FinderView finderView) {
		mFinderView = finderView;
	}

}
