package com.duowan.mobile.ixiaoshuo.utils;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.BaseActivity;

import java.io.File;

public class BookCoverDownloder extends TaskRunnable {
	private ImageView mImageView;
	private Book mBook;

	public BookCoverDownloder(Book book, ImageView imageView) {
		this.mBook = book;
		this.mImageView = imageView;
		mImageView.setTag(mBook.getBookId());
	}

	@Override
	public void run() {
		boolean result = NetService.get().downloadFile(mBook.getCoverUrl(), new File(mBook.getLocalCoverPath()));
		handler.sendEmptyMessage(result ? 1 : 0);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what != 1 || !validate()) return;
			Bitmap coverBitmap = BitmapUtil.loadBitmapInFile(mBook.getLocalCoverPath(), mImageView);
			if (coverBitmap != null) mImageView.setImageBitmap(coverBitmap);
		}
	};

	@Override
	boolean validate() {
		Object tagValue = mImageView.getTag();
		if (tagValue != null && tagValue instanceof Integer) {
			int imvTagBookId = (Integer) mImageView.getTag();
			// when fast scrolling, ImageView maybe refers to other Book quickly
			return mBook.getBookId() == imvTagBookId;
		}
		return false;
	}

	public static void loadCover(BaseActivity activity, Book book, ImageView imvBookCover) {
		String bookCoverPath = book.getLocalCoverPath();
		Bitmap coverBitmap = BitmapUtil.loadBitmapInFile(bookCoverPath, imvBookCover);
		if (coverBitmap != null) {
			imvBookCover.setImageBitmap(coverBitmap);
		} else {
			activity.submitTask(new BookCoverDownloder(book, imvBookCover));

			coverBitmap = BitmapUtil.loadBitmapInRes(R.drawable.cover_less, imvBookCover);
			imvBookCover.setImageBitmap(coverBitmap);
		}
	}

}
