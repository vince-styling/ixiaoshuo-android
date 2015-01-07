package com.vincestyling.ixiaoshuo.event;

import com.duowan.mobile.netroid.Listener;
import com.vincestyling.ixiaoshuo.db.AppDBOverseer;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.net.request.DeleteBookDBRequest;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Chapter;

import java.util.ArrayList;
import java.util.List;

public class ReaderSupport {
    private static OnDownloadChapterListener mListener;
    private static Chapter mReadingChapter;
    private static int mChapterCount;
    private static Book mBook;

    public static void init(int bookId) throws IllegalStateException {
        mBook = AppDBOverseer.get().getBookOnReading(bookId);
        if (mBook == null) throw new IllegalStateException("bookId not exists " + bookId);

        mReadingChapter = AppDBOverseer.get().getReadingChapter(mBook.getId());
        mReadingChapter.ready(mBook.getId());
    }

    public static void setDownloadChapterListener(OnDownloadChapterListener listener) {
        mListener = listener;
    }

    public static int getBookId() {
        return mBook.getId();
    }

    public static String getBookName() {
        return mBook.getName();
    }

    public static int getTotalChapterCount() {
        if (mChapterCount == 0)
            mChapterCount = AppDBOverseer.get().getBookChapterCount(mBook.getId());
        return mChapterCount;
    }

    public static int getUnReadChapterCount() {
        return getTotalChapterCount() - getCurrentChapterIndex() - 1;
    }

    public static int getCurrentChapterIndex() {
        return AppDBOverseer.get().getBookChapterIndex(mBook.getId(), mReadingChapter.getChapterId());
    }

    public static Chapter getCurrentChapter() {
        return mReadingChapter;
    }

    public static Chapter getPrevChapter() {
        Chapter chapter = AppDBOverseer.get().getPreviousChapter(mBook.getId(), mReadingChapter.getChapterId());
        if (chapter != null) chapter.ready(mBook.getId());
        return chapter;
    }

    public static Chapter getNextChapter() {
        Chapter chapter = AppDBOverseer.get().getNextChapter(mBook.getId(), mReadingChapter.getChapterId());
        if (chapter != null) chapter.ready(mBook.getId());
        return chapter;
    }

    public static List<Chapter> getChapterList() {
        List<Chapter> list = AppDBOverseer.get().getBookChapters(mBook.getId());
        List<Chapter> chapInfoList = new ArrayList<Chapter>(list.size());
        for (Chapter chapter : list) {
            chapInfoList.add(chapter);
        }
        return chapInfoList;
    }

    public static void onReadingChapter(Chapter chapter) {
        AppDBOverseer.get().makeReadingChapter(mBook.getId(), chapter);
        mReadingChapter = chapter;
    }

    public static boolean isBookOnShelf() {
        return AppDBOverseer.get().isBookOnShelf(mBook.getId());
    }

    public static boolean addToBookShelf() {
        return AppDBOverseer.get().addToBookShelf(mBook.getId());
    }

    public static void removeInBookShelf() {
        new DeleteBookDBRequest(mBook);
    }

    public static boolean downloadOneChapter(final Chapter chapter) {
        if (chapter == null || chapter.isNativeChapter()) {
            return false;
        }

        // onDownloadStart() should perform with Netroid's Listener.onPreExecute(),
        // but onPreExecute() always delay, doesn't make the UI know it instantly.
        mListener.onDownloadStart(chapter);

        Netroid.downloadChapterContent(mBook.getId(), chapter.getChapterId(), new Listener<Void>() {
            @Override
            public void onFinish() {
                mListener.onDownloadComplete(chapter);
            }
            @Override
            public void onSuccess(Void r) {
                chapter.ready(mBook.getId());
            }
        });

        return true;
    }

    public static void destory() {
        mReadingChapter = null;
        mChapterCount = 0;
        mListener = null;
        mBook = null;
    }
}
