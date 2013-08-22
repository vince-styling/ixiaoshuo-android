package com.duowan.mobile.ixiaoshuo.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;

public abstract class ViewBuilder {
	protected int mViewId;
	protected ViewGroup mView;
	protected MainActivity mActivity;

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

	protected abstract void build();

	protected View findViewById(int viewId) {
		return mView.findViewById(viewId);
	}

	public int getViewId() {
		return mViewId;
	}

	public boolean isReusable() {
		return true;
	}

}
