package com.vincestyling.ixiaoshuo.reader;


import android.os.Bundle;
import android.view.KeyEvent;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.event.ChapterDownloader;
import com.vincestyling.ixiaoshuo.ui.MainMenuGridView;
import com.vincestyling.ixiaoshuo.ui.ScrollLayout;
import com.vincestyling.ixiaoshuo.utils.SysUtil;
import com.vincestyling.ixiaoshuo.view.ViewBuilder;

public class MainActivity extends BaseActivity {
	private ScrollLayout mLotMainContent;
	private MainMenuGridView mMainMenuView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mLotMainContent = (ScrollLayout) findViewById(R.id.lotMainContent);

		mMainMenuView = (MainMenuGridView) findViewById(R.id.mainMenuView);
		showMenuView(MainMenuGridView.MENU_BOOKSHELF);

		getReaderApplication().setMainActivity(this);
	}

	public void showMenuView(int menuId) {
		showView(mMainMenuView.buildViewBuilder(menuId));
	}

	public void showView(ViewBuilder builder) {
		mLotMainContent.showView(builder);
	}

	private long lastPressBackKeyTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (mLotMainContent.onKeyDown(keyCode, event)) return true;

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
