package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.duowan.mobile.ixiaoshuo.R;

public class CommonMenuDialog extends AbsDialog {
	public CommonMenuDialog(Context context) {
		this(context, "提示");
	}

	public CommonMenuDialog(Context context, String title) {
		super(context);

		mContentView = (ViewGroup) getLayoutInflater().inflate(R.layout.common_menu_dialog, null);
		if(title == null) {
			mContentView.findViewById(R.id.txtDialogTitle).setVisibility(View.GONE);
		} else {
			((TextView) mContentView.findViewById(R.id.txtDialogTitle)).setText(title);
		}
	}

	public void initContentView(MenuItem[] menus) {
		for (MenuItem menu : menus) {
			TextView menuView = (TextView) getLayoutInflater().inflate(R.layout.common_menu_dialog_item, mContentView, false);
			menuView.setOnClickListener(menu.clickEvent);
			menuView.setText(menu.title);
			mContentView.addView(menuView);
		}
		initContentView(0.8f);
	}

	public static class MenuItem {
		private String title;
		private View.OnClickListener clickEvent;
		public MenuItem(String title, View.OnClickListener clickEvent) {
			this.title = title;
			this.clickEvent = clickEvent;
		}
		public String getTitle() { return title; }
		public View.OnClickListener getClickEvent() { return clickEvent; }
	}

}
