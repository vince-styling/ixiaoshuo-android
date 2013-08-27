package com.duowan.mobile.ixiaoshuo.view;

import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;

public abstract class ViewBuilder {
	protected int mViewId;
	protected View mView;
	protected MainActivity mActivity;
	protected OnShowListener mShowListener;

	public View getView() {
		if (mView == null) {
			build();
			if (mView instanceof LinearLayout) {
				mView.setLayoutParams(
						new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.MATCH_PARENT));
			}
		}
		return mView;
	}

	// build the view
	protected abstract void build();

	// init view such as attach events or set visibility, should invoke after build(), because we will get view by Activity sometime
	public abstract void init();

	// when focus on, resume the view
	public void resume() {}

	// show view and do something like show event
	public void show() {
		mView.setVisibility(View.VISIBLE);
		if (mShowListener != null) mShowListener.onShow();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	protected final View findViewById(int viewId) {
		return mView.findViewById(viewId);
	}

	public final int getViewId() {
		return mViewId;
	}

	public boolean isReusable() {
		return true;
	}

	public final MainActivity getActivity() {
		return mActivity;
	}

	public static interface OnShowListener {
		void onShow();
	}

}
