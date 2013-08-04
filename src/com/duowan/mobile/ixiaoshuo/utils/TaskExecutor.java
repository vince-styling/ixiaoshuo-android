package com.duowan.mobile.ixiaoshuo.utils;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskExecutor {
	private ExecutorService service;

	public TaskExecutor(int numThreads) {
		service = Executors.newFixedThreadPool(numThreads);
	}

	public void executeTask(Runnable runnable) {
		service.execute(runnable);
	}

//	public <T> Future<T> submitTask(Callable<T> task) {
//		return service.submit(task);
//	}

	LinkedList<TaskRunnable> mTaskRunnableList = new LinkedList<TaskRunnable>();
	public void submitTask(TaskRunnable runnable) {
		mTaskRunnableList.add(runnable);
	}

	public void startExecute() {
		while (mTaskRunnableList.size() > 0) {
			TaskRunnable runnable = mTaskRunnableList.pollLast();
			if(runnable != null && runnable.validate()) executeTask(runnable);
		}
	}

	public void shutdown() {
		if (service != null) service.shutdown();
	}

}
