package com.duowan.mobile.ixiaoshuo.view;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import com.duowan.mobile.ixiaoshuo.reader.BaseActivity;

public abstract class ViewBuilder implements Animation.AnimationListener {
	protected int mViewId;
	protected View mView;
	protected boolean mIsInFront;
	private BaseActivity mActivity;
	protected OnShowListener mShowListener;
	private Animation mInAnim, mOutAnim;

	public View getView() {
		if (mView == null) {
			build();
			setLayout();
		}
		return mView;
	}

	protected void setLayout() {
		if (mView instanceof LinearLayout) {
			mView.setLayoutParams(
					new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT));
		}
	}

	// build the view
	protected abstract void build();

	// init view such as attach events or set visibility, should invoke after build(),
	// because we will get view by Activity sometime
	public void init() {}

	// always use resume() to show view
	public void resume() {
		bringToFront();
	}

	protected void bringToFront() {
		if (mIsInFront) return;
		mView.setVisibility(View.VISIBLE);
		if (mInAnim != null) mView.startAnimation(mInAnim);
		if (mShowListener != null) mShowListener.onShow();
		mIsInFront = true;
	}

	public void pushBack() {
		mIsInFront = false;
		if (mOutAnim != null) {
			mView.startAnimation(mOutAnim);
		} else {
			mView.setVisibility(View.GONE);
		}
		// if view cannot use again, we should be remove it
		if (!isReusable()) ((ViewGroup) mView.getParent()).removeView(mView);
	}

	public boolean isInFront() {
		return mIsInFront;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	protected View findViewById(int viewId) {
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

	public boolean compare(ViewBuilder builder) {
		return mViewId == builder.getViewId();
	}

	public BaseActivity getActivity() {
		return mActivity;
	}

	public void setActivity(BaseActivity activity) {
		mActivity = activity;
	}

	public void setInAnimation(int animId) {
		mInAnim = AnimationUtils.loadAnimation(mActivity, animId);
	}

	public void setOutAnimation(int animId) {
		mOutAnim = AnimationUtils.loadAnimation(mActivity, animId);
		mOutAnim.setAnimationListener(this);
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		mView.setVisibility(View.GONE);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	public static interface OnShowListener {
		void onShow();
	}

}
