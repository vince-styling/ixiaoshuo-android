package com.duowan.mobile.ixiaoshuo.pojo;

public class BookOnUpdate {
	int bookId, websizeId, lastChapterId;

	public BookOnUpdate(int bookId, int websizeId, int lastChapterId) {
		this.bookId = bookId;
		this.websizeId = websizeId;
		this.lastChapterId = lastChapterId;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public int getWebsizeId() {
		return websizeId;
	}

	public void setWebsizeId(int websizeId) {
		this.websizeId = websizeId;
	}

	public int getLastChapterId() {
		return lastChapterId;
	}

	public void setLastChapterId(int lastChapterId) {
		this.lastChapterId = lastChapterId;
	}
}
