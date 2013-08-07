package com.duowan.mobile.ixiaoshuo.utils;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskExecutor {
	LinkedList<TaskRunnable> mTaskRunnableList = new LinkedList<TaskRunnable>();
	private ExecutorService service;
	private boolean isSuspend;

	public TaskExecutor(int numThreads) {
		service = Executors.newFixedThreadPool(numThreads);
	}

	public void submitTask(TaskRunnable runnable) {
		if(isSuspend) {
			mTaskRunnableList.add(runnable);
		} else {
			service.execute(runnable);
		}
	}

	public void suspend() {
		isSuspend = true;
	}

	public void startExecute() {
		isSuspend = false;
		while (mTaskRunnableList.size() > 0) {
			if (isSuspend) break;
			TaskRunnable runnable = mTaskRunnableList.pollLast();
			if (runnable != null && runnable.validate()) submitTask(runnable);
		}
	}

	public void shutdown() {
		if (service != null) service.shutdown();
	}

}
