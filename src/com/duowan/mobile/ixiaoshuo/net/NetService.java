package com.duowan.mobile.ixiaoshuo.net;

import android.content.Context;
import com.duowan.mobile.ixiaoshuo.pojo.BookOnUpdate;
import com.duowan.mobile.ixiaoshuo.utils.IOUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.List;

public final class NetService extends BaseNetService {
	private NetService() {}

	private static NetService mInstance;
	public static NetService get() {
		return mInstance;
	}

	/** must init with application startup */
	public static synchronized void init(Context context) {
		if (mInstance != null) return;
		mInstance = new NetService();
		mInstance.doInit(context);
	}

	public void syncChapterUpdateOnBookshelf(List<BookOnUpdate> bookList) {
		try {
			String pageName = "/bookshelf/get_chapter_update.do";
			String params = NetUtil.convertListToParams(bookList, "bookList");

			// TODO : 明天开始测试；respond 的 status、message 都没用，建议服务端直接返回 data。
			HttpResponse response = executeGet(pageName, params);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String json = new String(IOUtil.toByteArray(entity.getContent()));
				System.out.println(json);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
