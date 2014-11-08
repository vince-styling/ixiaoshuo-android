package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;

public class CommonMenuDialog extends AbsDialog {
    public CommonMenuDialog(Context context) {
        this(context, R.string.common_dialog_title);
    }

    public CommonMenuDialog(Context context, int titleResId) {
        this(context, context.getResources().getString(titleResId));
    }

    public CommonMenuDialog(Context context, String title) {
        super(context);

        mContentView = (ViewGroup) getLayoutInflater().inflate(R.layout.common_menu_dialog, null);
        if (title != null) {
            ((TextView) mContentView.findViewById(R.id.txvDialogTitle)).setText(title);
        } else {
            mContentView.findViewById(R.id.txvDialogTitle).setVisibility(View.GONE);
        }
    }

    public void initContentView(MenuItem... menus) {
        for (int i = 0; i < menus.length; i++) {
            TextView menuView = (TextView) getLayoutInflater().inflate(
                    R.layout.common_menu_dialog_item, mContentView, false);
            if (i + 1 == menus.length) menuView.setBackgroundResource(
                    R.drawable.common_menu_dialog_bottom_item_bg_selector);
            menuView.setOnClickListener(menus[i].clickEvent);
            menuView.setText(menus[i].titleResId);
            mContentView.addView(menuView);
        }
        initContentView(.8f);
    }

    public static class MenuItem {
        private View.OnClickListener clickEvent;
        private int titleResId;

        public MenuItem(int titleResId, View.OnClickListener clickEvent) {
            this.clickEvent = clickEvent;
            this.titleResId = titleResId;
        }
    }
}
