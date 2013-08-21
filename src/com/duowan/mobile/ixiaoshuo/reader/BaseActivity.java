package com.duowan.mobile.ixiaoshuo.reader;

import android.app.Activity;
import com.duowan.mobile.ixiaoshuo.event.TaskRunnable;

public abstract class BaseActivity extends Activity {

	public ReaderApplication getReaderApplication() {
		return (ReaderApplication) super.getApplication();
	}

	public void suspendTaskExecutor() {
		getReaderApplication().getTaskExecutor().suspend();
	}

	public void submitTask(TaskRunnable runnable) {
		getReaderApplication().getTaskExecutor().submitTask(runnable);
	}

	public void startTasksExecute() {
		getReaderApplication().getTaskExecutor().startExecute();
	}

	public void showToastMsg(CharSequence msg) {
		getReaderApplication().showToastMsg(msg);
	}

	public void showToastMsg(int resId) {
		getReaderApplication().showToastMsg(resId);
	}

}
