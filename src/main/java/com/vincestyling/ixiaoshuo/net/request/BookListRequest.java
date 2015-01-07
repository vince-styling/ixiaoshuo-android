package com.vincestyling.ixiaoshuo.net.request;

import com.duowan.mobile.netroid.Listener;
import com.vincestyling.asqliteplus.PaginationList;
import com.vincestyling.ixiaoshuo.net.Respond;
import com.vincestyling.ixiaoshuo.pojo.Book;

public class BookListRequest extends NetworkRequest<PaginationList<Book>> {

    public BookListRequest(String url, Listener<PaginationList<Book>> listener) {
        super(url, listener);
    }

    @Override
    protected PaginationList<Book> convert(Respond respond) {
        PaginationList<Book> bookList = respond.convertPaginationList(Book.class);
        if (bookList == null || bookList.size() == 0) return null;
        return bookList;
    }

}
