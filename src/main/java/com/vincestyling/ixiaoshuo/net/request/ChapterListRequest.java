package com.vincestyling.ixiaoshuo.net.request;

import com.duowan.mobile.netroid.Listener;
import com.vincestyling.asqliteplus.PaginationList;
import com.vincestyling.ixiaoshuo.net.Respond;
import com.vincestyling.ixiaoshuo.pojo.Chapter;

public class ChapterListRequest extends NetworkRequest<PaginationList<Chapter>> {

    public ChapterListRequest(String url, Listener<PaginationList<Chapter>> listener) {
        super(url, listener);
    }

    @Override
    protected PaginationList<Chapter> convert(Respond respond) {
        PaginationList<Chapter> chapterList = respond.convertPaginationList(Chapter.class);
        if (chapterList == null || chapterList.size() == 0) return null;
        return chapterList;
    }

}
