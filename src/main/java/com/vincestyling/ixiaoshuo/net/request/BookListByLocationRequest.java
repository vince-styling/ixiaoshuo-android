package com.vincestyling.ixiaoshuo.net.request;

import com.duowan.mobile.netroid.Listener;
import com.vincestyling.asqliteplus.PaginationList;
import com.vincestyling.ixiaoshuo.net.Respond;
import com.vincestyling.ixiaoshuo.pojo.BookByLocation;

public class BookListByLocationRequest extends NetworkRequest<PaginationList<BookByLocation>> {

    public BookListByLocationRequest(String url, Listener<PaginationList<BookByLocation>> listener) {
        super(url, listener);
    }

    @Override
    protected PaginationList<BookByLocation> convert(Respond respond) {
        PaginationList<BookByLocation> bookList = respond.convertPaginationList(BookByLocation.class);
        if (bookList == null || bookList.size() == 0) return null;
        return bookList;
    }

}
