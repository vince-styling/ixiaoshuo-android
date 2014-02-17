package com.vincestyling.ixiaoshuo.net;

import com.duowan.mobile.netroid.RequestQueue;
import com.duowan.mobile.netroid.request.ImageRequest;
import com.duowan.mobile.netroid.toolbox.ImageLoader;
import com.vincestyling.ixiaoshuo.pojo.Const;

import java.util.concurrent.TimeUnit;

public class SelfImageLoader extends ImageLoader {

	public SelfImageLoader(RequestQueue queue) {
		super(queue);
	}

	@Override
	public ImageRequest buildRequest(String requestUrl, int maxWidth, int maxHeight) {
		ImageRequest request = new ImageRequest(requestUrl, maxWidth, maxHeight);
		request.setCacheSequence(Const.HTTP_CACHE_KEY_MEMORY, Const.HTTP_CACHE_KEY_DISK);
		request.setCacheExpireTime(TimeUnit.DAYS, 10);
		return request;
	}

}
