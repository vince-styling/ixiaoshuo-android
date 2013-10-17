package com.duowan.mobile.ixiaoshuo.reader;

import android.app.Application;
import android.widget.Toast;
import com.duowan.mobile.ixiaoshuo.db.AppDAO;
import com.duowan.mobile.ixiaoshuo.event.MainHandler;
import com.duowan.mobile.ixiaoshuo.net.NetService;

public class ReaderApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		AppDAO.init(this);
		NetService.init(this);
		mMainHandler = new MainHandler();
		//CrashHandler.getInstance().init(this);
	}

	MainActivity mMainActivity;
	public void setMainActivity(MainActivity mainActivity) {
		mMainActivity = mainActivity;
	}
	public MainActivity getMainActivity() {
		return mMainActivity;
	}

	private Toast mToast;
	public void showToastMsg(CharSequence msg) {
		if(mToast != null) mToast.cancel();
		mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		mToast.show();
	}
	public void showToastMsg(int resId) {
		if(mToast != null) mToast.cancel();
		mToast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
		mToast.show();
	}

	private MainHandler mMainHandler;
	public MainHandler getMainHandler() {
		return mMainHandler;
	}

}
