package com.vincestyling.ixiaoshuo.view.bookshelf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.ui.TopTabIndicator;
import com.vincestyling.ixiaoshuo.utils.AppLog;
import com.vincestyling.ixiaoshuo.view.BaseFragment;
import com.vincestyling.ixiaoshuo.view.PageIndicator;

public class BookshelfView extends BaseFragment {
	public static final int PAGER_INDEX = 0;

	private ViewPager mShelfPager;
	private MyAdapter mAdapter;
	private PageIndicator mIndicator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.book_shelf, container, false);

		mAdapter = new MyAdapter(getActivity().getSupportFragmentManager());
		mShelfPager = (ViewPager) view.findViewById(R.id.shelfPager);
		mShelfPager.setAdapter(mAdapter);

		mIndicator = (TopTabIndicator) view.findViewById(R.id.pageIndicator);
		mIndicator.setViewPager(mShelfPager);

		return view;
	}

	private FragmentCreator[] mMenus = {
			new FragmentCreator(R.string.type_txt, BookshelfTextListView.class),
			new FragmentCreator(R.string.type_voice, BookshelfVoiceListView.class),
			new FragmentCreator(R.string.type_local, BookshelfLocalListView.class)
	};

	private class MyAdapter extends FragmentStatePagerAdapter {
		public MyAdapter(FragmentManager fm) {
			super(fm);
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
			if (resId > 0) {
				return getResources().getString(resId);
			}
			return null;
		}
	}

}
