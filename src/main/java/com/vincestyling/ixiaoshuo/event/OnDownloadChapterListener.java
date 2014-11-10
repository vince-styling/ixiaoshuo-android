package com.vincestyling.ixiaoshuo.event;

import com.vincestyling.ixiaoshuo.pojo.Chapter;

public interface OnDownloadChapterListener {
    public void onDownloadStart(Chapter chapter);
    public void onDownloadComplete(Chapter chapter);
}
