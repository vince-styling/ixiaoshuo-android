package com.duowan.mobile.ixiaoshuo.view;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;

public abstract class ViewBuilder {
	protected int mViewId;
	protected View mView;
	protected boolean mIsInFront;
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

	// init view such as attach events or set visibility, should invoke after build(),
	// because we will get view by Activity sometime
	public abstract void init();

	// always use resume() to show view
	public void resume() {
		bringToFront();
	}

	protected void bringToFront() {
		if (mIsInFront) return;
		if (mShowListener != null) mShowListener.onShow();
		mView.setVisibility(View.VISIBLE);
		mIsInFront = true;
	}

	public void pushBack() {
		mIsInFront = false;
		mView.setVisibility(View.GONE);
		// if view cannot use again, we should be remove it
		if (!isReusable()) ((ViewGroup) mView.getParent()).removeView(mView);
	}

	public boolean isInFront() {
		return mIsInFront;
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

	protected boolean isReusable() {
		return true;
	}

	// when responding back key event, we'll ask this method before show view again
	public boolean canShowBack() {
		return true;
	}

	public final MainActivity getActivity() {
		return mActivity;
	}

	public static interface OnShowListener {
		void onShow();
	}

}
