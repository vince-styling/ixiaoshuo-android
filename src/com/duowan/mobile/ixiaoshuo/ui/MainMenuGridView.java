package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.view.BookFinderView;
import com.duowan.mobile.ixiaoshuo.view.BookshelfView;

public class MainMenuGridView extends SingleLineGridView {
	public MainMenuGridView(Context context) {
		super(context);
	}

	public MainMenuGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public static final int MENU_BOOKSHELF	= 10;
	public static final int MENU_FINDER		= 20;
	public static final int MENU_DETECTOR 	= 30;
	public static final int MENU_SEARCH		= 40;

	@Override
	protected void init() {
		mGridItems = new SparseArray<GridItem>(4);

		mGridItems.put(MENU_BOOKSHELF, new GridItem(R.drawable.menu_bookshelf_on, R.drawable.menu_bookshelf_off, new ClickEvent() {
			public void onClick() {
				getActivity().showView(new BookshelfView(getActivity()));
			}
		}));

		mGridItems.put(MENU_FINDER, new GridItem(R.drawable.menu_finder_on, R.drawable.menu_finder_off, new ClickEvent() {
			public void onClick() {
				getActivity().showView(new BookFinderView(getActivity()));
			}
		}));

		mGridItems.put(MENU_DETECTOR, new GridItem(R.drawable.menu_detector_on, R.drawable.menu_detector_off, new ClickEvent() {
			public void onClick() {
				getActivity().showToastMsg("点击了雷达菜单");
			}
		}));

		mGridItems.put(MENU_SEARCH, new GridItem(R.drawable.menu_search_on, R.drawable.menu_search_off, new ClickEvent() {
			public void onClick() {
				getActivity().showToastMsg("点击了搜索菜单");
			}
		}));

		mPaddingTop = getResources().getDimensionPixelSize(R.dimen.globalMenuPadding);

		mHighlightDrawable = getResources().getDrawable(R.drawable.menu_bg_pressed);

		mSelectedItemId = MENU_BOOKSHELF;
	}

}
