package com.vincestyling.ixiaoshuo.reader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.event.ChapterDownloader;
import com.vincestyling.ixiaoshuo.utils.SysUtil;
import com.vincestyling.ixiaoshuo.view.BaseFragment;
import com.vincestyling.ixiaoshuo.view.ViewBuilder;
import com.vincestyling.ixiaoshuo.view.bookshelf.BookshelfView;

public class MainActivity extends BaseActivity {
	private ViewPager mMainContentPager;
//	private MainMenuGridView mMainMenuView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mMainContentPager = (ViewPager) findViewById(R.id.mainContentPager);
		mMainContentPager.setAdapter(new MyAdapter(getSupportFragmentManager()));

//		mMainMenuView = (MainMenuGridView) findViewById(R.id.mainMenuView);
//		showMenuView(MainMenuGridView.MENU_BOOKSHELF);
	}

	// If adjust Fragment order, remember reset the index for each Fragment index which inside them.
	private BaseFragment.FragmentCreator[] mMenus = {
			new BaseFragment.FragmentCreator(BookshelfView.class),
//			new BaseFragment.FragmentCreator(FinderView.class),
//			new BaseFragment.FragmentCreator(DetectorView.class),
//			new BaseFragment.FragmentCreator(SearchView.class),
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
	}

	public void showMenuView(int menuId) {
//		showView(mMainMenuView.buildViewBuilder(menuId));
	}

	public void showView(ViewBuilder builder) {
//		mLotMainContent.showView(builder);
	}

	private long lastPressBackKeyTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
//				if (mLotMainContent.onKeyDown(keyCode, event)) return true;

				long currentTime = System.currentTimeMillis();
				if(currentTime - lastPressBackKeyTime < 2000) {
					ChapterDownloader.get().cancelAll();

					finish();
					SysUtil.killAppProcess();
				} else {
					lastPressBackKeyTime = currentTime;
					showToastMsg("再按一次退出程序");
				}
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
