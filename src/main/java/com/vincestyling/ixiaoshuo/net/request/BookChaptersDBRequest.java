package com.vincestyling.ixiaoshuo.net.request;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.NetworkResponse;
import com.duowan.mobile.netroid.Response;
import com.vincestyling.asqliteplus.PaginationList;
import com.vincestyling.ixiaoshuo.db.AppDBOverseer;
import com.vincestyling.ixiaoshuo.pojo.Chapter;

public class BookChaptersDBRequest extends DBRequest<PaginationList<Chapter>> {
    private PaginationList<Chapter> chapterList;
    private int bookId, pageNo;

    public BookChaptersDBRequest(int bookId, int pageNo, Listener<PaginationList<Chapter>> listener) {
        super(listener);
        this.bookId = bookId;
        this.pageNo = pageNo;
    }

    @Override
    protected void onPerform() {
        chapterList = AppDBOverseer.get().getSimpleBookChapters(bookId, pageNo, 100);
    }

    @Override
    protected Response<PaginationList<Chapter>> parseNetworkResponse(NetworkResponse response) {
        if (chapterList != null && chapterList.size() > 0)
            return Response.success(chapterList, response);
        return Response.error(new NetroidError());
    }
}
