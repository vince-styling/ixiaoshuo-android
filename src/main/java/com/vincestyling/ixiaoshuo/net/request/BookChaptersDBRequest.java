package com.vincestyling.ixiaoshuo.net.request;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.NetworkResponse;
import com.duowan.mobile.netroid.Response;
import com.vincestyling.ixiaoshuo.db.AppDAO;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.utils.PaginationList;

public class BookChaptersDBRequest extends BasicRequest<PaginationList<Chapter>> {
    private PaginationList<Chapter> chapterList;
    private int bookId, pageNo;

    public BookChaptersDBRequest(int bookId, int pageNo, Listener<PaginationList<Chapter>> listener) {
        super(null, listener);
        this.bookId = bookId;
        this.pageNo = pageNo;
    }

    @Override
    public NetworkResponse perform() {
        chapterList = AppDAO.get().getSimpleBookChapters(bookId, pageNo, 100);
        return new NetworkResponse(null, null);
    }

    @Override
    protected Response<PaginationList<Chapter>> parseNetworkResponse(NetworkResponse response) {
        if (chapterList != null && chapterList.size() > 0)
            return Response.success(chapterList, response);
        return Response.error(new NetroidError());
    }
}