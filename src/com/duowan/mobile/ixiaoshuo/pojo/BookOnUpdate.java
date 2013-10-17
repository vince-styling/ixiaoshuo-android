package com.duowan.mobile.ixiaoshuo.pojo;

public class BookOnUpdate {
	int bookId, lastChapterId;

	public BookOnUpdate() {}
	public BookOnUpdate(int bookId, int lastChapterId) {
		this.bookId = bookId;
		this.lastChapterId = lastChapterId;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public int getLastChapterId() {
		return lastChapterId;
	}

	public void setLastChapterId(int lastChapterId) {
		this.lastChapterId = lastChapterId;
	}

}
