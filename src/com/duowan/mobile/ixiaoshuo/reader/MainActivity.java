package com.duowan.mobile.ixiaoshuo.reader;

import android.os.Bundle;
import android.view.KeyEvent;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.ui.MainMenuGridView;
import com.duowan.mobile.ixiaoshuo.ui.ScrollLayout;
import com.duowan.mobile.ixiaoshuo.utils.SysUtil;
import com.duowan.mobile.ixiaoshuo.view.ViewBuilder;

public class MainActivity extends BaseActivity {
	private ScrollLayout mLotMainContent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mLotMainContent = (ScrollLayout) findViewById(R.id.lotMainContent);

		MainMenuGridView mainMenuView = (MainMenuGridView) findViewById(R.id.mainMenuView);
		showView(mainMenuView.buildViewBuilder(MainMenuGridView.MENU_BOOKSHELF));
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
