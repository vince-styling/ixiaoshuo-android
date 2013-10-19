package com.duowan.mobile.ixiaoshuo.doc;

import android.util.Log;
import com.duowan.mobile.ixiaoshuo.event.YYReader;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.ui.RenderPaint;
import com.duowan.mobile.ixiaoshuo.utils.Encoding;
import com.duowan.mobile.ixiaoshuo.utils.IOUtil;
import com.duowan.mobile.ixiaoshuo.utils.Paths;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.zip.ZipInputStream;

public class OnlineDocument extends Document {
	private File contentTempFile;
	private YYReader.OnDownloadChapterListener mOnDownloadChapterListener;
	private OnTurnChapterListener mOnTurnChapterListener;
	private boolean mIsTurnPrevious;

	public OnlineDocument(RenderPaint paint,
						  YYReader.OnDownloadChapterListener onDownloadChapterListener,
						  OnTurnChapterListener onTurnChapterListener) throws Exception {
		super(paint);

		mOnDownloadChapterListener = onDownloadChapterListener;
		mOnTurnChapterListener = onTurnChapterListener;
		mEncoding = Encoding.GBK;

		contentTempFile = new File(Paths.getCacheDirectoryPath() + ".tmp");
		if (!contentTempFile.canRead()) contentTempFile.createNewFile();
		mRandBookFile = new RandomAccessFile(contentTempFile, "r");
	}

	private boolean renewRandomAccessFile(Chapter chapter) {
		if (chapter == null) return false;
		try {
			if (chapter.isNativeChapter()) {
				FileInputStream fins = new FileInputStream(new File(chapter.getFilePath()));
				ZipInputStream zins = new ZipInputStream(fins);

				if (zins.getNextEntry() == null) {
					zins.close();
					fins.close();
					return false;
				}

				byte[] data = IOUtil.toByteArray(zins);
				mFileSize = data.length;

				FileOutputStream fos = new FileOutputStream(contentTempFile);
				fos.write(data);
				fos.close();

				fins.close();

				mByteMetaList.clear();
				mContentBuf.setLength(0);
				mPageCharOffsetInBuffer = 0;

				YYReader.onReadingChapter(chapter);
				mOnTurnChapterListener.onTurnChapter();
				return true;
			}

			if (chapter.isRemoteChapter() && !isDownloading()) {
				YYReader.downloadOneChapter(chapter, mOnDownloadChapterListener);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return false;
	}

	@Override
	public float calculateReadingProgress() {
		int chapterCount = YYReader.getTotalChapterCount();
		int readChapterNum = YYReader.onGetCurrentChapterIndex() + 1;
		if (readChapterNum == chapterCount && mReadByteEndOffset == mFileSize) {
			if (mPageCharOffsetInBuffer + mMaxCharCountPerPage >= mContentBuf.length()) return 100;
		}
		return readChapterNum * 1.0f / chapterCount * 100;
	}

	@Override
	public boolean adjustReadingProgress(Chapter chapter) {
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

		Chapter chapter = YYReader.getNextChapterInfo();
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
	public void onDownloadComplete(boolean result, boolean willAdjust) {
		super.onDownloadComplete(result, willAdjust);
		if (result && willAdjust) return;
		mIsTurnPrevious = false;
	}

	@Override
	public String getReadingInfo() {
		return YYReader.getCurrentChapterInfo().getTitle();
	}

	public static interface OnTurnChapterListener {
		void onTurnChapter();
	}

}
