package com.duowan.mobile.ixiaoshuo.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.duowan.mobile.ixiaoshuo.R;

public class CommonMenuDialog extends AbsDialog {
	public CommonMenuDialog(Context context) {
		super(context);
	}

	public CommonMenuDialog(Context context, MenuItem[] menus) {
		this(context, "提示", menus);
	}

	public CommonMenuDialog(Context context, String title, MenuItem[] menus) {
		super(context);

		mContentView = (ViewGroup) getLayoutInflater().inflate(R.layout.common_menu_dialog, null);
		if(title == null) {
			mContentView.findViewById(R.id.txtDialogTitle).setVisibility(View.GONE);
		} else {
			((TextView) mContentView.findViewById(R.id.txtDialogTitle)).setText(title);
		}

		for (MenuItem menu : menus) {
			TextView menuView = (TextView) getLayoutInflater().inflate(R.layout.common_menu_dialog_item, mContentView, false);
			menuView.setText(menu.title);
			menuView.setOnClickListener(menu.clickEvent);
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
