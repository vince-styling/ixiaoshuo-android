package com.duowan.mobile.ixiaoshuo.doc;

import android.util.Log;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
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
	private String bookDirectoryPath;
	private ProcessCallback mProcessCallback;
	
	public OnlineDocument(Book book, RenderPaint paint, ProcessCallback callback) throws Exception {
		super(book, paint);

		mEncoding = Encoding.GBK;
		mProcessCallback = callback;

		bookDirectoryPath = Paths.getCacheDirectorySubFolderPath(mBook.getBookId());
		contentTempFile = new File(bookDirectoryPath + ".tmp");
		if (!contentTempFile.canRead()) contentTempFile.createNewFile();
		mRandBookFile = new RandomAccessFile(contentTempFile, "r");

		adjustReadingProgress(mBook.getReadingChapter());
	}

	private boolean renewRandomAccessFile(Chapter chapter) {
		if (chapter == null) return false;
		try {
			// TODO : 有可能即将要读的这个章节跟当前在读的章节是同一章
			String fileName = String.valueOf(chapter.getId());
			File chapterFile = new File(bookDirectoryPath + fileName);

			// check cache file exists
			if (chapterFile.length() > 100) {
				FileInputStream fins = new FileInputStream(chapterFile);
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
				System.gc();
			} else {
				mProcessCallback.fetchChapter(chapter);
				return false;
			}

			mByteMetaList.clear();
			mContentBuf.setLength(0);
			mPageCharOffsetInBuffer = 0;
			mBook.makeChapterReading(chapter);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return false;
		}
		return true;
	}

	@Override
	public boolean adjustReadingProgress(Chapter chapter) {
		if (renewRandomAccessFile(chapter)) {
			mReadByteBeginOffset = chapter.getBeginPosition();
			mReadByteEndOffset = mReadByteBeginOffset;
			invalidatePrevPagesCache();
			scrollDownBuffer();
			return true;
		}
		return false;
	}

	@Override
	public boolean turnNextPage() {
		if (super.turnNextPage()) return true;
		Chapter chapter = mBook.getNextChapter();
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
		Chapter chapter = mBook.getPreviousChapter();
		if (renewRandomAccessFile(chapter)) {
			mReadByteBeginOffset = getBackmostPosition();
			mReadByteEndOffset = mReadByteBeginOffset;
			scrollDownBuffer();
			return true;
		}
		return false;
	}

	public static interface ProcessCallback {
		public void fetchChapter(Chapter chapter);
	}
	
}
