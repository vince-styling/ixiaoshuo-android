package com.duowan.mobile.ixiaoshuo.reader;

import android.os.Bundle;
import android.view.KeyEvent;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.ui.ScrollLayout;
import com.duowan.mobile.ixiaoshuo.view.BookshelfView;
import com.duowan.mobile.ixiaoshuo.view.ViewBuilder;

public class MainActivity extends BaseActivity {
	private ScrollLayout mLotMainContent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mLotMainContent = (ScrollLayout) findViewById(R.id.lotMainContent);
		mLotMainContent.showView(new BookshelfView(MainActivity.this));
	}

	public void showView(ViewBuilder builder) {
		mLotMainContent.showView(builder);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (mLotMainContent.onKeyDown(keyCode, event)) return true;
				break;
		}
		return super.onKeyDown(keyCode, event);
	}

}
