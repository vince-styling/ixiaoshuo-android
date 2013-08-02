package com.duowan.mobile.ixiaoshuo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;

public class ScrollLayout extends LinearLayout {
	public ScrollLayout(Context context) {
		super(context);
	}

	public ScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private ViewBuilder[] mViewBuilders;

	Animation mSlideLeftIn, mSlideLeftOut, mSlideRightIn, mSlideRightOut;

	public void setViewBuilders(ViewBuilder[] viewBuilders) {
		this.mViewBuilders = viewBuilders;
	}

	public void showView(Class<? extends ViewBuilder> viewClass) {
		ViewBuilder targetBuilder = null;
		for (ViewBuilder builder : mViewBuilders) {
			if (builder.getClass() == viewClass) {
				targetBuilder = builder;
			} else {
				View view = findViewById(builder.getViewId());
				if (view != null) view.setVisibility(View.GONE);
			}
		}
		if (targetBuilder == null) return;

		View view = findViewById(targetBuilder.getViewId());
		if (view != null) {
			for (int i = 0; i < getChildCount(); i++) {
				View childView = getChildAt(i);
				childView.setVisibility(childView == view ? View.VISIBLE : View.GONE);
			}
		} else {
			addView(targetBuilder.getView());
		}
	}

}
