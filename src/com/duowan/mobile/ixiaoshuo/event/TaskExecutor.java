package com.duowan.mobile.ixiaoshuo.event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskExecutor {
	private ExecutorService service;

	private static TaskExecutor mInstance;
	public static TaskExecutor get() {
		if (mInstance == null) mInstance = new TaskExecutor(6);
		return mInstance;
	}

	private TaskExecutor(int numThreads) {
		service = Executors.newFixedThreadPool(numThreads);
	}

	public void execute(TaskRunnable runnable) {
		service.execute(runnable);
	}

	public void shutdown() {
		if (service != null) service.shutdown();
	}

}
