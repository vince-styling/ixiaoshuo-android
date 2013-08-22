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

//		final Button mBtnMenuBookshelf, mBtnMenuFinder, mBtnMenuDetect, mBtnMenuSearch;
//		mBtnMenuBookshelf = (Button) findViewById(R.id.btnMenuBookshelf);
//		mBtnMenuFinder = (Button) findViewById(R.id.btnMenuFinder);
//		mBtnMenuDetect = (Button) findViewById(R.id.btnMenuDetect);
//		mBtnMenuSearch = (Button) findViewById(R.id.btnMenuSearch);

//		mBtnMenuBookshelf.setOnClickListener(new View.OnClickListener(){
//			public void onClick(View btnView) {
//				mBtnMenuBookshelf.setBackgroundResource(R.drawable.menu_bookshelf_layer_list);
//			}
//		});
//		mBtnMenuFinder.setOnClickListener(this);
//		mBtnMenuDetect.setOnClickListener(this);
//		mBtnMenuSearch.setOnClickListener(this);
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
