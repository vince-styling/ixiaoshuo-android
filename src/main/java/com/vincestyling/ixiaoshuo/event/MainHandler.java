package com.vincestyling.ixiaoshuo.event;

import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

public class MainHandler extends Handler {
    private SparseArray<Notifier> mNotifierArray;

    public MainHandler() {
        mNotifierArray = new SparseArray<Notifier>(5);
    }

    public void handleMessage(Message msg) {
        ((Notifier) msg.obj).onNotified();
    }

    public void putNotifier(int key, Notifier notifier) {
        mNotifierArray.put(key, notifier);
    }

    public void sendMessage(int key) {
        sendMessage(mNotifierArray.get(key));
    }

    public void sendMessage(Notifier notifier) {
        if (notifier != null) {
            Message msg = Message.obtain();
            msg.obj = notifier;
            sendMessage(msg);
        }
    }

}
