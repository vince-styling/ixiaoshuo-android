package com.vincestyling.ixiaoshuo.view.bookshelf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.ui.TopTabIndicator;
import com.vincestyling.ixiaoshuo.view.BaseFragment;
import com.vincestyling.ixiaoshuo.view.FragmentCreator;
import com.vincestyling.ixiaoshuo.view.PageIndicator;

public class BookshelfView extends BaseFragment {
    public static final int PAGER_INDEX = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.book_shelf, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewPager shelfPager = (ViewPager) view.findViewById(R.id.shelfPager);
        shelfPager.setAdapter(new MyAdapter());

        PageIndicator indicator = (TopTabIndicator) view.findViewById(R.id.pageIndicator);
        indicator.setViewPager(shelfPager);
    }

    private FragmentCreator[] mMenus = {
            new FragmentCreator(R.string.type_txt, BookshelfTextListView.class),
            new FragmentCreator(R.string.type_voice, BookshelfVoiceListView.class),
            new FragmentCreator(R.string.type_local, BookshelfLocalListView.class)
    };

    private class MyAdapter extends FragmentStatePagerAdapter {
        public MyAdapter() {
            super(getChildFragmentManager());
        }

        @Override
        public int getCount() {
            return mMenus.length;
        }

        @Override
        public Fragment getItem(int position) {
            return mMenus[position].newInstance();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int resId = mMenus[position].getTitleResId();
            return resId > 0 ? getResources().getString(resId) : null;
        }
    }

}
