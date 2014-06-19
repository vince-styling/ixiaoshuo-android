package com.vincestyling.ixiaoshuo.view.finder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.ui.FinderTabIndicator;
import com.vincestyling.ixiaoshuo.view.BaseFragment;
import com.vincestyling.ixiaoshuo.view.FragmentCreator;
import com.vincestyling.ixiaoshuo.view.PageIndicator;

public class FinderSimplyView extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.finder_content, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ViewPager finderPager = (ViewPager) view.findViewById(R.id.finderPager);
		finderPager.setAdapter(new MyAdapter());

		PageIndicator indicator = (FinderTabIndicator) view.findViewById(R.id.multilistIndicator);
		indicator.setViewPager(finderPager);
	}

	private FragmentCreator[] mMenus = {
			new FragmentCreator(FinderNewlyBookView.class),
			new FragmentCreator(FinderHottestBookView.class),
			new FragmentCreator(FinderUpdatesBookView.class),
			new FragmentCreator(FinderCategoriesView.class),
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
	}

}
