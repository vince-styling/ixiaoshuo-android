package com.vincestyling.ixiaoshuo.net.request;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetworkResponse;

public abstract class DBRequest<T> extends BasicRequest<T> {
    public DBRequest(Listener<T> listener) {
        super(listener);
    }

    @Override
    public NetworkResponse perform() {
        onPerform();
        return new NetworkResponse(null, null);
    }

    protected abstract void onPerform();
}
