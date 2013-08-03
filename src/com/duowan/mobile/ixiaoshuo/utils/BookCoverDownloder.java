package com.duowan.mobile.ixiaoshuo.utils;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;

import java.io.File;

public class BookCoverDownloder implements Runnable {
	private ImageView mImageView;
	private Book mBook;

	public BookCoverDownloder(Book book, ImageView imageView) {
		this.mBook = book;
		this.mImageView = imageView;
		mImageView.setTag(mBook.getId());
	}

	@Override
	public void run() {
		boolean result = NetService.get().downloadFile(mBook.getCoverUrl(), new File(mBook.getLocalCoverPath()));
		handler.sendEmptyMessage(result ? 1 : 0);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what != 1) return;
			Object tagValue = mImageView.getTag();
			if (tagValue != null && tagValue instanceof Integer) {
				int imvTagBookId = (Integer) mImageView.getTag();
				// when fast scrolling, ImageView maybe refers to other Book quickly
				if (mBook.getId() == imvTagBookId) {
					Bitmap coverBitmap = BitmapUtil.loadBitmapInFile(mBook.getLocalCoverPath(), mImageView);
					if (coverBitmap != null) mImageView.setImageBitmap(coverBitmap);
				}
			}
		}
	};

}
