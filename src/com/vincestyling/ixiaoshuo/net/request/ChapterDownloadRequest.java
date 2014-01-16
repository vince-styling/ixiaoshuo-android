package com.vincestyling.ixiaoshuo.net.request;

import com.duowan.mobile.netroid.*;
import com.vincestyling.ixiaoshuo.utils.AppLog;
import com.vincestyling.ixiaoshuo.utils.IOUtil;

public class ChapterDownloadRequest extends Request<Void> {
	private int bookId;
	private int chapterId;

	public ChapterDownloadRequest(int bookId, int chapterId, String url, Listener<Void> listener) {
		super(url, listener);
		this.bookId = bookId;
		this.chapterId = chapterId;
	}

	@Override
	protected Response<Void> parseNetworkResponse(NetworkResponse response) {
		try {
			String content = new String(response.data, response.charset);
			if (IOUtil.saveBookChapter(bookId, chapterId, content)) {
				return Response.success(null, response);
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
