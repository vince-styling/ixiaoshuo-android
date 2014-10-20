package com.vincestyling.ixiaoshuo.reader;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

public abstract class BaseActivity extends FragmentActivity {

    public ReaderApplication getReaderApplication() {
        return (ReaderApplication) super.getApplication();
    }

    public void showToastMsg(CharSequence msg) {
        getReaderApplication().showToastMsg(msg);
    }

    public void showToastMsg(String format, Object... args) {
        getReaderApplication().showToastMsg(String.format(format, args));
    }

    public void showToastMsg(int resId) {
        getReaderApplication().showToastMsg(resId);
    }

    public void showToastMsg(int resId, Object... args) {
        getReaderApplication().showToastMsg(String.format(getString(resId), args));
    }

    protected void onResume() {
        super.onResume();
        getReaderApplication().setCurrentActivity(this);
    }

    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences() {
        Activity currActivity = getReaderApplication().getCurrentActivity();
        if (currActivity != null && currActivity.equals(this))
            getReaderApplication().setCurrentActivity(null);
    }
}
