package com.duowan.mobile.ixiaoshuo.reader;

import android.app.Application;
import android.widget.Toast;
import com.duowan.mobile.ixiaoshuo.db.AppDAO;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.utils.TaskExecutor;
import com.duowan.mobile.ixiaoshuo.utils.TaskRunnable;

public class ReaderApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		AppDAO.init(this);
		NetService.init(this);
		mTaskExecutor = new TaskExecutor(10);
	}

	TaskExecutor mTaskExecutor;

	public void suspendTaskExecutor() {
		mTaskExecutor.suspend();
	}

	public void submitTask(TaskRunnable runnable) {
		mTaskExecutor.submitTask(runnable);
	}

	public void startTasksExecute() {
		mTaskExecutor.startExecute();
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

}
