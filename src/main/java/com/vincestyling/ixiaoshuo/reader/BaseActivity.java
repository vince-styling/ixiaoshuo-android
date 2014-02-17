package com.vincestyling.ixiaoshuo.reader;

import android.app.Activity;

public abstract class BaseActivity extends Activity {

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
