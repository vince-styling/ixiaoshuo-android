package com.vincestyling.ixiaoshuo.net.request;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.NetworkResponse;
import com.duowan.mobile.netroid.Response;
import com.vincestyling.ixiaoshuo.utils.AppLog;
import com.vincestyling.ixiaoshuo.utils.IOUtil;

public class ChapterDownloadRequest extends BasicRequest<Void> {
    private int bookId, chapterId;

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

            return Response.error(new NetroidError(String.format(
                    "ChapterDownloadRequest{chapterId=%d, bookId=%d, contentL=%d}",
                    chapterId, bookId, content.length())));
        } catch (Exception e) {
            AppLog.e(e);
            return Response.error(new NetroidError(e));
        }
    }
}
