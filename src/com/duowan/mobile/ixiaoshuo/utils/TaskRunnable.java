package com.duowan.mobile.ixiaoshuo.utils;

public abstract class TaskRunnable implements Runnable {
	// before run, TaskExecutor will invoke it
	abstract boolean validate();
}
