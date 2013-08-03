package com.duowan.mobile.ixiaoshuo.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskExecutor {
	private ExecutorService service;

	public TaskExecutor(int numThreads) {
		service = Executors.newFixedThreadPool(numThreads);
	}

	public void executeTask(Runnable runnable) {
		service.execute(runnable);
	}

	public <T> Future<T> submitTask(Callable<T> task) {
		return service.submit(task);
	}

	public void shutdown() {
		if (service != null) service.shutdown();
	}

}
