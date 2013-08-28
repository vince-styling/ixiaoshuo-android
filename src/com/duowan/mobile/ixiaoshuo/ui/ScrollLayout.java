package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.duowan.mobile.ixiaoshuo.view.ViewBuilder;

public class ScrollLayout extends LinearLayout {
	public ScrollLayout(Context context) {
		super(context);
	}

	public ScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void showView(ViewBuilder builder) {
		for (int i = 0; i < getChildCount(); i++) {
			((ViewBuilder) getChildAt(i).getTag()).pushBack();
		}

		View view = findViewById(builder.getViewId());
		if (view != null) {
			if (builder.isReusable()) {
				((ViewBuilder) view.getTag()).bringToFront();
			} else addView(builder);
		} else addView(builder);
	}

	private void addView(ViewBuilder builder) {
		addView(builder.getView());
		builder.init();
		builder.bringToFront();
		builder.getView().setTag(builder);
	}

	public void resumeView() {
		for (int index = 0; index < getChildCount(); index++) {
			ViewBuilder builder = (ViewBuilder) getChildAt(index).getTag();
			if (builder.isInFront()) builder.resume();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				for (int index = 0; index < getChildCount(); index++) {
					View childView = getChildAt(index);
					ViewBuilder builder = (ViewBuilder) childView.getTag();
					if (builder.isInFront()) {
						// dispatch key event to child view(maybe also ScrollLayout)
						if (builder.onKeyDown(keyCode, event)) return true;

						// if current shown view was top
						if (index == 0) return false;

						// hide current view
						builder.pushBack();

						// get back view and show it
						builder = (ViewBuilder) getChildAt(--index).getTag();
						builder.bringToFront();

						return true;
					}
				}
				break;
		}
		return false;
	}

}
