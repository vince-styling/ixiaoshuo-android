package com.vincestyling.ixiaoshuo.net.request;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.vincestyling.ixiaoshuo.net.Netroid;

public abstract class BasicRequest<T> extends Request<T> {
    public BasicRequest(int method, String url, Listener<T> listener) {
        super(method, url, listener);
        // deploy request immediately
        Netroid.get().add(this);
    }

    public BasicRequest(String url, Listener<T> listener) {
        this(Method.GET, url, listener);
    }

    public BasicRequest(Listener<T> listener) {
        this(null, listener);
    }
}
