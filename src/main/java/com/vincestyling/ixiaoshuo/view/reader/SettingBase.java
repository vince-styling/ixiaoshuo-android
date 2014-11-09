package com.vincestyling.ixiaoshuo.view.reader;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import com.vincestyling.ixiaoshuo.reader.ReaderActivity;

public abstract class SettingBase {
    protected ReaderActivity mReaderActivity;
    protected int mViewId;
    protected View mView;

    protected SettingBase(ReaderActivity readerActivity, int viewId) {
        mReaderActivity = readerActivity;
        mViewId = viewId;
    }

    public void showView(ViewGroup lotSecondaryMenu, Animation inAnim) {
        for (int i = 0; i < lotSecondaryMenu.getChildCount(); i++) {
            View view = lotSecondaryMenu.getChildAt(i);
            view.setVisibility(View.GONE);
        }

        View view = lotSecondaryMenu.findViewById(mViewId);
        if (view != null) {
            ((SettingBase) view.getTag()).resume();
            view.setVisibility(View.VISIBLE);
        } else {
            build();
            resume();
            mView.setTag(this);
            lotSecondaryMenu.addView(mView);
        }

        if (lotSecondaryMenu.getVisibility() == View.GONE) {
            lotSecondaryMenu.setVisibility(View.VISIBLE);
            lotSecondaryMenu.startAnimation(inAnim);
        }
    }

    protected void inflate(int layoutResId) {
        mView = mReaderActivity.getLayoutInflater().inflate(layoutResId, null);
    }

    protected abstract void build();

    protected void resume() {}
}
