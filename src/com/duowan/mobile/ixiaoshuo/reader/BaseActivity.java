package com.duowan.mobile.ixiaoshuo.reader;

import android.app.Activity;

public abstract class BaseActivity extends Activity {

	public ReaderApplication getReaderApplication() {
		return (ReaderApplication) super.getApplication();
	}

}
