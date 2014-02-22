package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.util.AttributeSet;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.event.OnGridItemClickListener;
import com.vincestyling.ixiaoshuo.reader.MainActivity;
import com.vincestyling.ixiaoshuo.view.ViewBuilder;
import com.vincestyling.ixiaoshuo.view.bookshelf.BookshelfView;
import com.vincestyling.ixiaoshuo.view.detector.DetectorView;
import com.vincestyling.ixiaoshuo.view.finder.FinderView;
import com.vincestyling.ixiaoshuo.view.search.SearchView;

public class MainMenuGridView extends GridView {
	public static final int MENU_BOOKSHELF = 10;
	public static final int MENU_FINDER = 20;
	public static final int MENU_DETECTOR = 30;
	public static final int MENU_SEARCH = 40;

	public MainMenuGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void initGrid() {
		putItem(new GridItem(R.drawable.menu_bookshelf_on, R.drawable.menu_bookshelf_off, new OnGridItemClickListener(MENU_BOOKSHELF) {
			public void onGridItemClick() {
				getActivity().showMenuView(getGridItemId());
			}
		}));

		putItem(new GridItem(R.drawable.menu_finder_on, R.drawable.menu_finder_off, new OnGridItemClickListener(MENU_FINDER) {
			public void onGridItemClick() {
				getActivity().showMenuView(getGridItemId());
			}
		}));

		putItem(new GridItem(R.drawable.menu_detector_on, R.drawable.menu_detector_off, new OnGridItemClickListener(MENU_DETECTOR) {
			public void onGridItemClick() {
				getActivity().showMenuView(MENU_DETECTOR);
			}
		}));

		putItem(new GridItem(R.drawable.menu_search_on, R.drawable.menu_search_off, new OnGridItemClickListener(MENU_SEARCH) {
			public void onGridItemClick() {
				getActivity().showMenuView(getGridItemId());
			}
		}));

		mHighlightDrawable = getResources().getDrawable(R.drawable.menu_bg_pressed);
	}

	public ViewBuilder buildViewBuilder(int menuId) {
//		if (menuId == MENU_BOOKSHELF) {
//			return new BookshelfView(getActivity(), new ViewBuilder.OnShowListener() {
//				@Override
//				public void onShow() {
//					selectItem(MENU_BOOKSHELF);
//				}
//			});
//		}
//		if (menuId == MENU_FINDER) {
//			return new FinderView(getActivity(), new ViewBuilder.OnShowListener() {
//				@Override
//				public void onShow() {
//					selectItem(MENU_FINDER);
//				}
//			});
//		}
//		if (menuId == MENU_SEARCH) {
//			return new SearchView(getActivity(), new ViewBuilder.OnShowListener() {
//				@Override
//				public void onShow() {
//					selectItem(MENU_SEARCH);
//				}
//			});
//		}
//		if (menuId == MENU_DETECTOR) {
//			return new DetectorView(getActivity(), new ViewBuilder.OnShowListener() {
//				@Override
//				public void onShow() {
//					selectItem(MENU_DETECTOR);
//				}
//			});
//		}
		return null;
	}

	protected MainActivity getActivity() {
		return (MainActivity) getContext();
	}

}
