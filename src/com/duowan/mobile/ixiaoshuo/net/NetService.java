package com.duowan.mobile.ixiaoshuo.net;

import android.content.Context;
import com.duowan.mobile.ixiaoshuo.pojo.BookOnUpdate;
import org.apache.http.client.methods.HttpPost;
import org.codehaus.jackson.type.TypeReference;

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

	public List<BookOnUpdate> syncChapterUpdateOnBookshelf(final List<BookOnUpdate> bookList) {
		try {
			HttpPost httpPost = makeHttpPost("/bookshelf/get_chapter_update.do");
			NetUtil.putListToParams(httpPost, bookList, "bookList");
			Respond respond = handleHttpExecute(httpPost);
			if (respond != null) {
				return respond.convert(new TypeReference<List<BookOnUpdate>>(){});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
