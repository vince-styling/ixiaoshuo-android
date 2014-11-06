package com.vincestyling.ixiaoshuo.net.request;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.vincestyling.ixiaoshuo.net.Netroid;

public abstract class BasicRequest<T> extends Request<T> {
    public BasicRequest(int method, String url, Listener<T> listener) {
        super(method, url, listener);
        // deploy immediately
        deploy();
    }

    public BasicRequest(String url, Listener<T> listener) {
        super(url, listener);
        // deploy immediately
        deploy();
    }

    private void deploy() {
        Netroid.get().add(this);
    }
}
