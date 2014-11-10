package com.vincestyling.ixiaoshuo.doc;

import android.util.Log;
import com.vincestyling.ixiaoshuo.event.ReaderSupport;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.ui.RenderPaint;
import com.vincestyling.ixiaoshuo.utils.CharsetUtil;
import com.vincestyling.ixiaoshuo.utils.Encoding;
import com.vincestyling.ixiaoshuo.utils.StringUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;

/**
 * This class implements the logics to read a plain text file by block,
 * and provides funcionalities to turn next & previous page.
 * this class will ensure that memory usage be always kept to a certain limit
 * (i.e. will not grow unlimitedly even if the file is large, even in hundred metabytes.)
 */
public abstract class Document {
    protected static final String TAG = "Document";

    private int mCurContentHeight;

    protected Encoding mEncoding;

    protected RandomAccessFile mRandBookFile;
    protected long mFileSize;                // in bytes

    protected byte[] mByteBuffer = new byte[8192];
    protected StringBuilder mContentBuf = new StringBuilder();
    protected int mPageCharOffsetInBuffer;        // char offset in buffer (current char offset in mContentBuf)

    protected long mReadByteBeginOffset;        // current read buffer start byte position in the file
    protected long mReadByteEndOffset;            // current read buffer end byte position in the file
    protected long mPageBeginPosition;            // page byte begin position in file
    protected int mNewlineIndex;                // for storing index of newline when turning next or previous pages
    protected int mEndCharIndex;                // for storing end index of a certain paragraph when turning next or previous pages
    protected int mCharCountOfPage;                // for storing temporary values when getLine()

    // for storing byte count of each block we read when reading through the file
    // for prevent mContentBuf from unlimitedly growing, we need to throw away characters at the
    // head of mContentBuf along the way we read through the file, and mByteMetaList is for avoiding
    // cutting off half a GBK char or a UTF-8 char at the head of the buffer
    protected LinkedList<ByteMeta> mByteMetaList = new LinkedList<ByteMeta>();
    // for turning preivous page, for storing temporary offsets
    LinkedList<Integer> mCharOffsetList = new LinkedList<Integer>();

    protected final void scrollDownBuffer() {
        // throw away characters at the head of the buffer if some requrements are met
        if (mByteMetaList.size() > 0 && mPageCharOffsetInBuffer >= mByteMetaList.peek().charCount) {
            ByteMeta meta = mByteMetaList.removeFirst();
            mContentBuf.delete(0, meta.charCount);

            // "meta.charCount" characters were thrown away, so we have to minus the "offset in buffer" by "meta.charCount"
            mPageCharOffsetInBuffer -= meta.charCount;
            // increase the start byte offset where we start reading the file
            mReadByteBeginOffset = meta.byteOffset + meta.byteCount;
        }

        try {
            // position the file pointer at the end position of the last/previous read
            mRandBookFile.seek(mReadByteEndOffset);
            int lenRead = mRandBookFile.read(mByteBuffer);
            if (lenRead > 0) {
                // skip last incomplete bytes if there are some of them
                lenRead -= CharsetUtil.getByteCountOfLastIncompleteChar(mByteBuffer, lenRead, mEncoding);

                // append the text to the end of the buffer
                String content = new String(mByteBuffer, 0, lenRead, mEncoding.getName());
                mContentBuf.append(content);

                // store the meta data of the current read
                mByteMetaList.add(new ByteMeta(mReadByteEndOffset, lenRead, content.length()));
                // grow the end byte offset
                mReadByteEndOffset += lenRead;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    protected final void scrollUpBuffer() {
        try {
            // we read backwards at most "mByteBuffer.length" bytes each time
            long positionToSeek = mReadByteBeginOffset - mByteBuffer.length;
            int bytesToRead = mByteBuffer.length;
            if (positionToSeek < 0) {    // if we reach the beginning of the file
                positionToSeek = 0;
                bytesToRead = (int) mReadByteBeginOffset;
            }
            mRandBookFile.seek(positionToSeek);
            int lenRead = mRandBookFile.read(mByteBuffer, 0, bytesToRead);
            if (lenRead > 0) {
                int incompleteByteCount = 0;
                if (positionToSeek > 0) {    // if we are not at the beginning of the file
                    incompleteByteCount = CharsetUtil.getByteCountOfFirstIncompleteChar(mByteBuffer, lenRead, mEncoding);
                    if (incompleteByteCount > 0) {
                        lenRead -= incompleteByteCount;
                    }
                }
                String content = new String(mByteBuffer, incompleteByteCount, lenRead, mEncoding.getName());
                mContentBuf.insert(0, content);

                // since we are reading backwards(towards the beginning of the file), we need to decrease
                // the current "start byte offset" from where we start reading the file
                mReadByteBeginOffset -= lenRead;
                mPageCharOffsetInBuffer += content.length();
                // prepend the meta data to the beginning of the file
                mByteMetaList.addFirst(new ByteMeta(mReadByteBeginOffset, lenRead, content.length()));
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public boolean turnNextPage() {
        if (mPageCharOffsetInBuffer + mCharCountOfPage < mContentBuf.length()) {
            // for scrolldown(turning next page), we simply add up mCharOffsetOfPage to the current char offset
            mPageCharOffsetInBuffer += mCharCountOfPage;
            return true;
        }
        return false;
    }

    public boolean turnPreviousPage() {
        // check if we need to read some bytes and prepend them to the beginning of the buffer(if we are near the beginning of the buffer)
        if (mReadByteBeginOffset > 0 && mPageCharOffsetInBuffer - RenderPaint.get().getMaxCharCountPerPage() < 0) {
            scrollUpBuffer();
        }
        if (mPageCharOffsetInBuffer > 0) {
            mCharOffsetList.clear();
            // *previous* page should start from here *at most*(if the *previous* page contains mMaxCharCountPerPage characters, which is quite unusual)
            int beginCharOffset = mPageCharOffsetInBuffer - RenderPaint.get().getMaxCharCountPerPage();
            if (beginCharOffset < 0) {    // if we reach the beginning of the file
                beginCharOffset = 0;
            } else {
                // try finding a NEWLINE(paragraph boundary)
                while (beginCharOffset > 0) {
                    if (mContentBuf.lastIndexOf(StringUtil.NEW_LINE_STR, beginCharOffset) != -1)
                        break;
                    --beginCharOffset;
                }
            }
            // end(exclusive) of the *previous* page is the start(inclusive) of the current page
            int endCharOffset = mPageCharOffsetInBuffer;
            // if last character of the previous page is a newline, we MUST skip it, beacause this newline
            // is for separating the last paragraph of the previous page and the first paragraph of the
            // current page(if it does exist), we don't need it because we are going to segment the text
            // by paragraphs of the previous page(only the previous page, not the current page)
            if (mContentBuf.charAt(endCharOffset - 1) == StringUtil.NEW_LINE_CHAR) {
                --endCharOffset;    // ignore last newline of the previous page
            }
            // from the end to the beginning, we search for the first newline(of course, the actual *first* newline might have
            // already been skipped, then we are searching for the second one if you are confusing^_^)
            int lineCharOffset = beginCharOffset;
            int newlineOffset = mContentBuf.lastIndexOf(StringUtil.NEW_LINE_STR, endCharOffset - 1);

            // found a newline in previous page
            if (newlineOffset != -1) {
                lineCharOffset = newlineOffset + 1;

                int lineCount = 0;
                while (endCharOffset > beginCharOffset) {
                    if (lineCharOffset == endCharOffset) {
                        newlineOffset = mContentBuf.lastIndexOf(StringUtil.NEW_LINE_STR, endCharOffset - 1);
                        if (newlineOffset != -1) {
                            lineCharOffset = newlineOffset + 1;
                        } else {
                            lineCharOffset = beginCharOffset;
                        }

                        lineCount = 0;
                    }
                    int charCount = RenderPaint.get().breakText(mContentBuf, lineCharOffset, endCharOffset, lineCount == 0);

                    // note: addFirst
                    mCharOffsetList.addFirst(lineCharOffset);

                    lineCharOffset += charCount;
                    ++lineCount;
                    if (lineCharOffset == endCharOffset) {
                        while (--lineCount >= 0) {
                            // note: removeFirst and then add to the last
                            mCharOffsetList.add(mCharOffsetList.removeFirst());
                        }
                        if (mCharOffsetList.size() >= RenderPaint.get().getMaxPageLineCount()) break;

                        lineCharOffset = endCharOffset = newlineOffset;
                    }
                }
            } else {
                while (lineCharOffset < endCharOffset) {
                    int charCount = RenderPaint.get().breakText(mContentBuf, lineCharOffset, endCharOffset, false);
                    mCharOffsetList.addFirst(lineCharOffset);
                    lineCharOffset += charCount;
                }
            }

            int contentHeight = 0;
            for (int i = 0; i < mCharOffsetList.size(); ++i) {
                if (contentHeight > 0) {
                    boolean isNewline = mContentBuf.charAt(mCharOffsetList.get(i - 1) - 1) == StringUtil.NEW_LINE_CHAR;
                    if (isNewline) contentHeight += RenderPaint.get().getParagraphSpacing();
                    else contentHeight += RenderPaint.get().getLineSpacing();
                }
                contentHeight += RenderPaint.get().getTextHeight();
                if (contentHeight > RenderPaint.get().getRenderHeight()) {
                    mPageCharOffsetInBuffer = mCharOffsetList.get(i - 1);
                    break;
                } else if (i == 0) {
                    mPageCharOffsetInBuffer = mCharOffsetList.get(mCharOffsetList.size() - 1);
                }
            }

            return true;
        }
        return false;
    }

    public void prepareGetLines() {
        mCurContentHeight = 0;
        mCharCountOfPage = 0;

        // only do this check once per page
        if (mReadByteEndOffset < mFileSize && mPageCharOffsetInBuffer + RenderPaint.get().getMaxCharCountPerPage() > mContentBuf.length())
            scrollDownBuffer();

        // reset newline to the current offset
        mNewlineIndex = mPageCharOffsetInBuffer;
    }

    public final static byte GET_NEXT_LINE_FLAG_HAS_NEXT_LINE = 1;
    public final static byte GET_NEXT_LINE_FLAG_SHOULD_JUSTIFY = 1 << 1;
    public final static byte GET_NEXT_LINE_FLAG_PARAGRAPH_ENDS = 1 << 2;

    public final byte getNextLine(StringBuilder sb) {
        // reach the end of the file
        if (mPageCharOffsetInBuffer + mCharCountOfPage >= mContentBuf.length()) return 0;

        mCurContentHeight += RenderPaint.get().getTextHeight();
        if (mCurContentHeight > RenderPaint.get().getRenderHeight()) return 0;
        byte flags = GET_NEXT_LINE_FLAG_HAS_NEXT_LINE;

        int index = mPageCharOffsetInBuffer + mCharCountOfPage;
        if (index == mNewlineIndex) {
            mNewlineIndex = mContentBuf.indexOf(StringUtil.NEW_LINE_STR, mNewlineIndex);
            mEndCharIndex = (mNewlineIndex != -1) ? mNewlineIndex : mContentBuf.length();
        }

        boolean needIndent = false;
        if (RenderPaint.get().getFirstLineIndent().length() > 0) {
            if (index > 0) {
                needIndent = mContentBuf.charAt(index - 1) == StringUtil.NEW_LINE_CHAR;
            } else {
                needIndent = determineParagraphIndent();
            }
        }

        int charCount = RenderPaint.get().breakText(mContentBuf, index, mEndCharIndex, needIndent);
        if (charCount > 0) {
            sb.append(mContentBuf, index, index + charCount);
            LayoutUtil.trimWhiteSpaces(sb);
            mCharCountOfPage += charCount;

            // append indent if carriage return character before the line
            if (needIndent) sb.insert(0, RenderPaint.get().getFirstLineIndent());
        }

        int endIndex = mPageCharOffsetInBuffer + mCharCountOfPage;
        if (endIndex == mNewlineIndex) {
            ++mCharCountOfPage;
            ++mNewlineIndex;
            mCurContentHeight += RenderPaint.get().getParagraphSpacing();
            flags |= GET_NEXT_LINE_FLAG_PARAGRAPH_ENDS;
        } else {
            // justify when text fill whole line
            if (endIndex < mContentBuf.length()) {
                flags |= GET_NEXT_LINE_FLAG_SHOULD_JUSTIFY;
            }
            mCurContentHeight += RenderPaint.get().getLineSpacing();
        }

        return flags;
    }

    public void calculatePagePosition() {
        try {
            mPageBeginPosition = mReadByteBeginOffset + mContentBuf.substring(0, mPageCharOffsetInBuffer).getBytes(mEncoding.getName()).length;
            Chapter chapter = ReaderSupport.getCurrentChapter();
            chapter.setReadPosition((int) mPageBeginPosition);
            ReaderSupport.onReadingChapter(chapter);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

//	protected long calculatePosition(float percentage) {
//		return percentage >= 1f ? mFileSize - 200 : (long) (mFileSize * percentage);
//	}

    protected final long getSafetyPosition(long fileBeginPosition) {
        if (fileBeginPosition == 0) return 0;
        try {
            byte[] tempContentBuf = new byte[1024 * 4];
            mRandBookFile.seek(fileBeginPosition);
            int lenRead = mRandBookFile.read(tempContentBuf);

            int skippedBytes = CharsetUtil.getByteCountOfFirstIncompleteChar(tempContentBuf, lenRead, mEncoding);
            if (tempContentBuf[skippedBytes] == StringUtil.NEW_LINE_CHAR) {
                ++skippedBytes;
            } else if (tempContentBuf[skippedBytes] == '\r') {
                ++skippedBytes;
                if (tempContentBuf[skippedBytes] == StringUtil.NEW_LINE_CHAR)
                    ++skippedBytes;
            }
            return fileBeginPosition + skippedBytes;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return fileBeginPosition;
    }

    // determine carriage return character that before the contentBuffer,
    // if exists or is file beginning, we should use paragraph first line indent
    private boolean determineParagraphIndent() {
        try {
            long beginPosition = mReadByteBeginOffset - mEncoding.getMinCharLength();
            if (beginPosition < 0) return true;

            byte[] tempBytes = new byte[mEncoding.getMinCharLength()];
            mRandBookFile.seek(beginPosition);
            mRandBookFile.read(tempBytes);

            String charStr = new String(tempBytes, mEncoding.getName());
            return charStr.charAt(charStr.length() - 1) == StringUtil.NEW_LINE_CHAR;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    protected final long getBackmostPosition() {
        long beginPosition = mFileSize - Double.valueOf(RenderPaint.get().getMaxCharCountPerLine() * (RenderPaint.get().getMaxPageLineCount() / 3) * mEncoding.getMaxCharLength()).intValue();
        return beginPosition < 1 ? 0 : getSafetyPosition(beginPosition);
    }

    public String getReadingInfo() {
        return ReaderSupport.getBookName();
    }

    public abstract boolean turnToChapter(Chapter chapter);

    public abstract float calculateReadingProgress();

    private boolean mIsDownloading;

    public boolean isDownloading() {
        return mIsDownloading;
    }

    public void onDownloadComplete(boolean result, boolean willAdjust) {
        mIsDownloading = false;
    }

    public void onDownloadStart() {
        mIsDownloading = true;
    }

    class ByteMeta {
        long byteOffset;
        int byteCount;
        int charCount;

        public ByteMeta(long byteOffset, int byteCount, int charCount) {
            this.byteOffset = byteOffset;
            this.byteCount = byteCount;
            this.charCount = charCount;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (mRandBookFile != null) mRandBookFile.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        super.finalize();
    }

}
