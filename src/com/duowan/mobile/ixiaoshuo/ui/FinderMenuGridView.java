package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.view.BookFinderView;
import com.duowan.mobile.ixiaoshuo.view.BookshelfView;

public class FinderMenuGridView extends SingleLineGridView {
	public FinderMenuGridView(Context context) {
		super(context);
	}

	public FinderMenuGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public static final int MENU_NEW			= 10;
	public static final int MENU_HOT			= 20;
	public static final int MENU_FINISHED		= 30;
	public static final int MENU_CATEGORIES		= 40;

	@Override
	protected void init() {
		mGridItems = new SparseArray<GridItem>(4);

		mGridItems.put(MENU_NEW, new GridItem(R.drawable.finder_new_on, R.drawable.finder_new_off, new ClickEvent() {
			public void onClick() {
				getActivity().showToastMsg("点击了最新菜单");
			}
		}));

		mGridItems.put(MENU_HOT, new GridItem(R.drawable.finder_hot_on, R.drawable.finder_hot_off, new ClickEvent() {
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

		mSelectedItemId = MENU_NEW;
	}

}
