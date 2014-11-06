package com.vincestyling.ixiaoshuo.net.request;

import com.duowan.mobile.netroid.Listener;
import com.vincestyling.ixiaoshuo.net.Respond;
import com.vincestyling.ixiaoshuo.utils.StringUtil;

public class HotKeywordsRequest extends NetworkRequest<String[]> {

    public HotKeywordsRequest(String url, Listener<String[]> listener) {
        super(url, listener);
    }

    @Override
    protected String[] convert(Respond respond) {
        String data = respond.convert(String.class);
        if (StringUtil.isEmpty(data)) return null;
        return data.split(",");
    }

}
