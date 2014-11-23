package com.vincestyling.ixiaoshuo.reader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.PopupWindow;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.ui.MainMenuGridView;
import com.vincestyling.ixiaoshuo.utils.SysUtil;
import com.vincestyling.ixiaoshuo.view.FragmentCreator;
import com.vincestyling.ixiaoshuo.view.bookshelf.BookshelfView;
import com.vincestyling.ixiaoshuo.view.detector.DetectorView;
import com.vincestyling.ixiaoshuo.view.finder.FinderView;
import com.vincestyling.ixiaoshuo.view.search.SearchView;

public class MainActivity extends BaseActivity {
    private MainMenuGridView mMainMenuView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ViewPager mainContentPager = (ViewPager) findViewById(R.id.mainContentPager);
        mainContentPager.setAdapter(new MyAdapter(getSupportFragmentManager()));

        mMainMenuView = (MainMenuGridView) findViewById(R.id.mainMenuView);
        mMainMenuView.setViewPager(mainContentPager);
        mMainMenuView.selectItem(BookshelfView.PAGER_INDEX);
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

    public void showMenuView(int menuId) {
		mMainMenuView.selectItem(menuId);
    }

    private PopupWindow mGlobalMenu;

    private boolean showGlobalMenu() {
        if (mGlobalMenu == null) {
            View view = getLayoutInflater().inflate(R.layout.global_apps_menu, null);
            mGlobalMenu = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, false);
            mGlobalMenu.setAnimationStyle(android.R.style.Animation_Dialog);
        }
        if (mGlobalMenu.isShowing()) return false;
        mGlobalMenu.showAtLocation(mMainMenuView, Gravity.BOTTOM, 0, 0);
        return true;
    }

    public boolean hideGlobalMenu() {
        if (mGlobalMenu != null && mGlobalMenu.isShowing()) {
            mGlobalMenu.dismiss();
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            hideGlobalMenu();
        }
        return super.onTouchEvent(event);
    }

    private long lastPressBackKeyTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (!hideGlobalMenu()) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastPressBackKeyTime < 2000) {
                        finish();
                        SysUtil.killAppProcess();
                    } else {
                        showToastMsg(R.string.exit_app_tip);
                        lastPressBackKeyTime = currentTime;
                    }
                }
                return true;
            case KeyEvent.KEYCODE_MENU:
                if (!showGlobalMenu()) hideGlobalMenu();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
