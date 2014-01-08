package com.vincestyling.ixiaoshuo.event;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.NetService;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.reader.BaseActivity;
import com.vincestyling.ixiaoshuo.utils.BitmapUtil;
import com.vincestyling.ixiaoshuo.utils.StringUtil;

import java.io.File;

public class BookCoverLoader extends TaskRunnable {
	private MainHandler mMainHandler;
	private ImageView mImageView;
	private Book mBook;

	public BookCoverLoader(Book book, ImageView imageView, MainHandler mainHandler) {
		mBook = book;
		mImageView = imageView;
		mImageView.setTag(mBook.getSourceBookId());
		mMainHandler = mainHandler;
	}

	@Override
	void execute() {
		boolean result = NetService.get().downloadFile(mBook.getCoverUrl(), new File(mBook.getLocalCoverPath()));
		if (result) {
			mMainHandler.sendMessage(new Notifier() {
				public void onNotified() {
					if (validate()) {
						Bitmap coverBitmap = BitmapUtil.loadBitmapInFile(mBook.getLocalCoverPath(), mImageView);
						if (coverBitmap != null) mImageView.setImageBitmap(coverBitmap);
					}
				}
			});
		}
	}

	@Override
	boolean validate() {
		Object tagValue = mImageView.getTag();
		if (tagValue != null && tagValue instanceof Integer) {
			int imvTagBookId = (Integer) mImageView.getTag();
			// when fast scrolling, ImageView maybe refers to other Book quickly
			return mBook.getSourceBookId() == imvTagBookId;
		}
		return false;
	}

	public static void loadCover(BaseActivity activity, Book book, ImageView imvBookCover) {
		String bookCoverPath = book.getLocalCoverPath();
		Bitmap coverBitmap = BitmapUtil.loadBitmapInFile(bookCoverPath, imvBookCover);
		if (coverBitmap != null) {
			imvBookCover.setImageBitmap(coverBitmap);
		} else {
			if (StringUtil.isValidUrl(book.getCoverUrl())) {
				TaskExecutor.get().execute(new BookCoverLoader(book, imvBookCover, activity.getReaderApplication().getMainHandler()));
			}
			coverBitmap = BitmapUtil.loadBitmapInRes(R.drawable.cover_less, imvBookCover);
			imvBookCover.setImageBitmap(coverBitmap);
		}
	}

}
