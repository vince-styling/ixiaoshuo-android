package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.util.AttributeSet;
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
			getChildAt(i).setVisibility(View.GONE);
		}

		View view = findViewById(builder.getViewId());
		if (view != null) {
			if (builder.isReusable()) {
				view.setVisibility(View.VISIBLE);
			} else {
				removeView(view);
				addView(builder.getView());
			}
		} else {
			addView(builder.getView());
		}
	}

}
