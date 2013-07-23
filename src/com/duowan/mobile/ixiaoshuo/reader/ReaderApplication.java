package com.duowan.mobile.ixiaoshuo.reader;

import android.app.Application;
import android.widget.Toast;
import com.duowan.mobile.ixiaoshuo.net.NetService;

public class ReaderApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		NetService.init(this);
	}

	private Toast mToast;
	public void showToastMsg(CharSequence msg) {
		if(mToast != null) mToast.cancel();
		mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		mToast.show();
	}
}
