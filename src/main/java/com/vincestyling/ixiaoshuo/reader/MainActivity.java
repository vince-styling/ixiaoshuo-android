package com.vincestyling.ixiaoshuo.reader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.ui.MainMenuGridView;
import com.vincestyling.ixiaoshuo.utils.SysUtil;
import com.vincestyling.ixiaoshuo.view.FragmentCreator;
import com.vincestyling.ixiaoshuo.view.bookshelf.BookshelfView;
import com.vincestyling.ixiaoshuo.view.detector.DetectorView;
import com.vincestyling.ixiaoshuo.view.finder.FinderView;
import com.vincestyling.ixiaoshuo.view.search.SearchView;

public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ViewPager mainContentPager = (ViewPager) findViewById(R.id.mainContentPager);
        mainContentPager.setAdapter(new MyAdapter(getSupportFragmentManager()));

        MainMenuGridView mainMenuView = (MainMenuGridView) findViewById(R.id.mainMenuView);
        mainMenuView.setViewPager(mainContentPager);
    }

    // If adjust Fragment order, remember reset the index for each Fragment index which inside them.
    private FragmentCreator[] mMenus = {
            new FragmentCreator(BookshelfView.class),
            new FragmentCreator(FinderView.class),
            new FragmentCreator(DetectorView.class),
            new FragmentCreator(SearchView.class),
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

    private long lastPressBackKeyTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastPressBackKeyTime < 2000) {
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
