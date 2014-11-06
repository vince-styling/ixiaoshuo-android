package com.vincestyling.ixiaoshuo.net.request;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.NetworkResponse;
import com.duowan.mobile.netroid.Response;
import com.vincestyling.ixiaoshuo.net.GObjectMapper;
import com.vincestyling.ixiaoshuo.net.Respond;
import com.vincestyling.ixiaoshuo.utils.AppLog;

import java.util.concurrent.TimeUnit;

public abstract class NetworkRequest<T> extends BasicRequest<T> {

    public NetworkRequest(String url, Listener<T> listener) {
        super(url, listener);
        setCacheExpireTime(TimeUnit.MINUTES, 20);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, response.charset);
            Respond respond = GObjectMapper.get().readValue(json, Respond.class);
            if (Respond.isCorrect(respond)) {
                T data = convert(respond);
                if (data == null) {
                    return Response.error(new NetroidError("cannot convert respond data."));
                }
                return Response.success(data, response);
            } else {
                return Response.error(new NetroidError(respond.toString()));
            }
        } catch (Exception e) {
            AppLog.e(e);
            return Response.error(new NetroidError(e));
        }
    }

    protected abstract T convert(Respond respond);
}
