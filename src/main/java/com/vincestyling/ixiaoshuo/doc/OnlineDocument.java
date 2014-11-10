package com.vincestyling.ixiaoshuo.doc;

import com.vincestyling.ixiaoshuo.event.OnChangeReadingInfoListener;
import com.vincestyling.ixiaoshuo.event.ReaderSupport;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.ui.RenderPaint;
import com.vincestyling.ixiaoshuo.utils.AppLog;
import com.vincestyling.ixiaoshuo.utils.Encoding;

import java.io.RandomAccessFile;

public class OnlineDocument extends Document {
    private OnChangeReadingInfoListener mOnChangeReadingInfoListener;
    private boolean mIsTurnPrevious;

    public OnlineDocument(OnChangeReadingInfoListener onChangeReadingInfoListener) throws Exception {
        mOnChangeReadingInfoListener = onChangeReadingInfoListener;
        mEncoding = Encoding.GBK;
    }

    private boolean renewRandomAccessFile(Chapter chapter) {
        if (chapter == null) return false;
        try {
            if (chapter.isNativeChapter()) {
                if (mRandBookFile != null) mRandBookFile.close();
                mRandBookFile = new RandomAccessFile(chapter.getFilePath(), "r");
                mFileSize = mRandBookFile.length();

                mByteMetaList.clear();
                mContentBuf.setLength(0);
                mPageCharOffsetInBuffer = 0;

                ReaderSupport.onReadingChapter(chapter);
                mOnChangeReadingInfoListener.onChangeTopInfo(getReadingInfo());

                int unreadCount = ReaderSupport.getUnReadChapterCount();
                mOnChangeReadingInfoListener.onChangeBottomInfo(
                        unreadCount > 0 ? String.format("剩余%d章", unreadCount) : "暂无新章节");

                return true;
            }

            if (chapter.isRemoteChapter() && !isDownloading()) {
                ReaderSupport.downloadOneChapter(chapter);
            }
        } catch (Exception e) {
            AppLog.e(e);
        }
        return false;
    }

    @Override
    public boolean turnToChapter(Chapter chapter) {
        if (renewRandomAccessFile(chapter)) {
            mReadByteBeginOffset = mIsTurnPrevious ? getBackmostPosition() : chapter.getReadPosition();
            mReadByteEndOffset = mReadByteBeginOffset;
            mIsTurnPrevious = false;
            scrollDownBuffer();
            return true;
        }
        mIsTurnPrevious = false;
        return false;
    }

    @Override
    public boolean turnNextPage() {
        if (super.turnNextPage()) return true;

        Chapter chapter = ReaderSupport.getNextChapter();
        if (renewRandomAccessFile(chapter)) {
            mReadByteBeginOffset = 0;
            mReadByteEndOffset = 0;
            scrollDownBuffer();
            return true;
        }

        return false;
    }

    @Override
    public boolean turnPreviousPage() {
        if (super.turnPreviousPage()) return true;

        Chapter chapter = ReaderSupport.getPrevChapter();
        if (renewRandomAccessFile(chapter)) {
            mReadByteBeginOffset = getBackmostPosition();
            mReadByteEndOffset = mReadByteBeginOffset;
            scrollDownBuffer();
            return true;
        }

        mIsTurnPrevious = isDownloading();
        return false;
    }

//    @Override
//    public void calculatePagePosition() {
//        super.calculatePagePosition();
//        float percentage = calculateReadingProgress();
//        percentage = percentage > 100 ? 100 : percentage;
//    }

    @Override
    public float calculateReadingProgress() {
        int chapterCount = ReaderSupport.getTotalChapterCount();
        int readChapterNum = ReaderSupport.getCurrentChapterIndex() + 1;
        if (readChapterNum == chapterCount && mReadByteEndOffset == mFileSize) {
            if (mPageCharOffsetInBuffer + RenderPaint.get().getMaxCharCountPerPage() >= mContentBuf.length()) return 100;
        }
        return readChapterNum * 1.0f / chapterCount * 100;
    }

    @Override
    public void onDownloadComplete(boolean result, boolean willAdjust) {
        super.onDownloadComplete(result, willAdjust);
        if (result && willAdjust) return;
        mIsTurnPrevious = false;
    }

    @Override
    public String getReadingInfo() {
        return ReaderSupport.getCurrentChapter().getTitle();
    }
}
