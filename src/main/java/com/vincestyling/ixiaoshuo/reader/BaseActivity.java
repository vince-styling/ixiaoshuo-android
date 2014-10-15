package com.vincestyling.ixiaoshuo.reader;

import android.support.v4.app.FragmentActivity;

public abstract class BaseActivity extends FragmentActivity {

    public ReaderApplication getReaderApplication() {
        return (ReaderApplication) super.getApplication();
    }

    public void showToastMsg(CharSequence msg) {
        getReaderApplication().showToastMsg(msg);
    }

    public void showToastMsg(int resId) {
        getReaderApplication().showToastMsg(resId);
    }

}
