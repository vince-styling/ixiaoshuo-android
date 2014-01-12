package com.vincestyling.ixiaoshuo.net;

import com.duowan.mobile.netroid.*;
import com.vincestyling.ixiaoshuo.utils.AppLog;
import com.vincestyling.ixiaoshuo.utils.IOUtil;

public class ChapterDownloadRequest extends Request<Void> {
	private int chapterId;
	private int bookId;

	public ChapterDownloadRequest(int bookId, int chapterId, String url, Response.Listener<Void> listener) {
		super(Method.GET, url, listener);
		this.chapterId = chapterId;
		this.bookId = bookId;
	}

	@Override
	protected Response<Void> parseNetworkResponse(NetworkResponse response) {
		try {
			String content = new String(response.data, response.charset);
			if (IOUtil.saveBookChapter(bookId, chapterId, content)) {
				return Response.success(null, new Cache.Entry(response.data, response.charset));
			}

			return Response.error(new NetroidError("ChapterDownloadRequest{" +
					"chapterId=" + chapterId +
					", bookId=" + bookId +
					", contentL=" + content.length() +
					'}'));
		} catch (Exception e) {
			AppLog.e(e);
			return Response.error(new NetroidError(e));
		}
	}

}
