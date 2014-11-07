package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.event.OnGridItemClickListener;
import com.vincestyling.ixiaoshuo.view.bookshelf.BookshelfView;
import com.vincestyling.ixiaoshuo.view.detector.DetectorView;
import com.vincestyling.ixiaoshuo.view.finder.FinderView;
import com.vincestyling.ixiaoshuo.view.search.SearchView;

public class MainMenuGridView extends GridView {
    private ViewPager mMainContentPager;

    public MainMenuGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setViewPager(ViewPager mainContentPager) {
        mMainContentPager = mainContentPager;
    }

    @Override
    protected void initGrid() {
        putItem(new GridItem(R.drawable.menu_bookshelf_on, R.drawable.menu_bookshelf_off, new OnGridItemClickListener(BookshelfView.PAGER_INDEX) {
            public void onGridItemClick() {
                mMainContentPager.setCurrentItem(getGridItemId());
            }
        }));

        putItem(new GridItem(R.drawable.menu_finder_on, R.drawable.menu_finder_off, new OnGridItemClickListener(FinderView.PAGER_INDEX) {
            public void onGridItemClick() {
                mMainContentPager.setCurrentItem(getGridItemId());
            }
        }));

        putItem(new GridItem(R.drawable.menu_detector_on, R.drawable.menu_detector_off, new OnGridItemClickListener(DetectorView.PAGER_INDEX) {
            public void onGridItemClick() {
                mMainContentPager.setCurrentItem(getGridItemId());
            }
        }));

        putItem(new GridItem(R.drawable.menu_search_on, R.drawable.menu_search_off, new OnGridItemClickListener(SearchView.PAGER_INDEX) {
            public void onGridItemClick() {
                mMainContentPager.setCurrentItem(getGridItemId());
            }
        }));

        mHighlightDrawable = getResources().getDrawable(R.drawable.menu_bg_pressed);
    }

    @Override
    public boolean selectItem(int itemId) {
        mMainContentPager.setCurrentItem(itemId, true);
        return super.selectItem(itemId);
    }
}
