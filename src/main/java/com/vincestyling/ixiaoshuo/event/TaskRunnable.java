package com.vincestyling.ixiaoshuo.event;

public abstract class TaskRunnable implements Runnable {

    @Override
    public final void run() {
        if (validate()) execute();
    }

    abstract void execute();

    boolean validate() {
        return true;
    }

}
