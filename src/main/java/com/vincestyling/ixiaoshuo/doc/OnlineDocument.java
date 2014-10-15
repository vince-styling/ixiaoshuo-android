package com.vincestyling.ixiaoshuo.doc;

import com.vincestyling.ixiaoshuo.event.OnChangeReadingInfoListener;
import com.vincestyling.ixiaoshuo.event.YYReader;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.ui.RenderPaint;
import com.vincestyling.ixiaoshuo.utils.AppLog;
import com.vincestyling.ixiaoshuo.utils.Encoding;

import java.io.RandomAccessFile;

public class OnlineDocument extends Document {
    private YYReader.OnDownloadChapterListener mOnDownloadChapterListener;
    private OnChangeReadingInfoListener mOnChangeReadingInfoListener;
    private boolean mIsTurnPrevious;

    public OnlineDocument(YYReader.OnDownloadChapterListener onDownloadChapterListener, OnChangeReadingInfoListener onChangeReadingInfoListener) throws Exception {
        mOnChangeReadingInfoListener = onChangeReadingInfoListener;
        mOnDownloadChapterListener = onDownloadChapterListener;
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

                YYReader.onReadingChapter(chapter);
                mOnChangeReadingInfoListener.onChangeBottomInfo("剩余" + YYReader.getUnReadChapterCount() + "章");
                mOnChangeReadingInfoListener.onChangeTopInfo(getReadingInfo());

                YYReader.downloadChapters();
                return true;
            }

            if (chapter.isRemoteChapter() && !isDownloading()) {
                YYReader.downloadOneChapter(chapter, mOnDownloadChapterListener);
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

        Chapter chapter = YYReader.getNextChapter();
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

        Chapter chapter = YYReader.getPrevChapter();
        if (renewRandomAccessFile(chapter)) {
            mReadByteBeginOffset = getBackmostPosition();
            mReadByteEndOffset = mReadByteBeginOffset;
            scrollDownBuffer();
            return true;
        }

        mIsTurnPrevious = isDownloading();
        return false;
    }

    @Override
    public void calculatePagePosition() {
        super.calculatePagePosition();
        float percentage = calculateReadingProgress();
        percentage = percentage > 100 ? 100 : percentage;
        YYReader.onReadingPercent(percentage);
    }

    @Override
    public float calculateReadingProgress() {
        int chapterCount = YYReader.getTotalChapterCount();
        int readChapterNum = YYReader.onGetCurrentChapterIndex() + 1;
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
        return YYReader.getCurrentChapter().getTitle();
    }

}
