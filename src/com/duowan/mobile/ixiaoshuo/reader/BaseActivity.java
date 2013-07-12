package com.duowan.mobile.ixiaoshuo.reader;

import android.app.Activity;

public class BaseActivity extends Activity {

	public ReaderApplication getReaderApplication() {
		return (ReaderApplication) super.getApplication();
	}

}
