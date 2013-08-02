package com.duowan.mobile.ixiaoshuo.reader;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.view.BookSearchView;
import com.duowan.mobile.ixiaoshuo.view.BookshelfView;
import com.duowan.mobile.ixiaoshuo.view.ScrollLayout;
import com.duowan.mobile.ixiaoshuo.view.ViewBuilder;

public class MainActivity extends BaseActivity {
	private ScrollLayout mLotMainContent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mLotMainContent = (ScrollLayout) findViewById(R.id.lotMainContent);
		mLotMainContent.setViewBuilders(new ViewBuilder[] {
				new BookshelfView(MainActivity.this),
				new BookSearchView(MainActivity.this)
		});
		mLotMainContent.showView(BookshelfView.class);

		RadioGroup btnMenuSwitchGrp = (RadioGroup) findViewById(R.id.btnMenuSwitchGrp);
		btnMenuSwitchGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedBtnId) {
				switch (checkedBtnId) {
					case R.id.btnMenuBookshelf:
						mLotMainContent.showView(BookshelfView.class);
						break;
					case R.id.btnMenuOnline:
						mLotMainContent.showView(BookSearchView.class);
						break;
					case R.id.btnMenuSettings:
						showToastMsg("点击了设置菜单");
						break;
				}
			}
		});
	}

	public void addView(View view) {
		for (int i = 0; i < mLotMainContent.getChildCount(); i++) {
			mLotMainContent.getChildAt(i).setVisibility(View.GONE);
		}
		mLotMainContent.addView(view);
	}

}
