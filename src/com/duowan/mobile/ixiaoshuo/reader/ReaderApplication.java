package com.duowan.mobile.ixiaoshuo.reader;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;
import com.duowan.mobile.ixiaoshuo.db.AppDAO;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.utils.TaskExecutor;

public class ReaderApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		AppDAO.init(this);
		NetService.init(this);
		mTaskExecutor = new TaskExecutor(10);
	}

	TaskExecutor mTaskExecutor;
	public TaskExecutor getTaskExecutor() {
		return mTaskExecutor;
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

	private Handler mBookShelfHandler;
	public void setBookShelfRefreshHandler(Handler handler) {
		mBookShelfHandler = handler;
	}
	public void sendBookShelfRefreshMessage() {
		if(mBookShelfHandler == null) return;
		mBookShelfHandler.sendEmptyMessage(0);
	}

}
