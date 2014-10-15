package com.vincestyling.ixiaoshuo.net;

import com.duowan.mobile.netroid.RequestQueue;
import com.duowan.mobile.netroid.request.ImageRequest;
import com.duowan.mobile.netroid.toolbox.ImageLoader;

import java.util.concurrent.TimeUnit;

public class SelfImageLoader extends ImageLoader {

    public SelfImageLoader(RequestQueue queue, ImageCache cache) {
        super(queue, cache);
    }

    @Override
    public ImageRequest buildRequest(String requestUrl, int maxWidth, int maxHeight) {
        ImageRequest request = new ImageRequest(requestUrl, maxWidth, maxHeight);
        request.setCacheExpireTime(TimeUnit.DAYS, 10);
        return request;
    }

}
