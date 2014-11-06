package com.vincestyling.ixiaoshuo.net.request;

import com.duowan.mobile.netroid.Listener;
import com.vincestyling.ixiaoshuo.net.Respond;
import com.vincestyling.ixiaoshuo.pojo.Book;

public class BookInfoRequest extends NetworkRequest<Book> {

    public BookInfoRequest(String url, Listener<Book> listener) {
        super(url, listener);
    }

    @Override
    protected Book convert(Respond respond) {
        return respond.convert(Book.class);
    }

}
