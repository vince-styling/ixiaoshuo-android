package com.duowan.mobile.ixiaoshuo.pojo;

public class BookOnUpdate {
	int bookId, websiteId, lastChapterId;

	public BookOnUpdate() {}
	public BookOnUpdate(int bookId, int websiteId, int lastChapterId) {
		this.bookId = bookId;
		this.websiteId = websiteId;
		this.lastChapterId = lastChapterId;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public int getWebsiteId() {
		return websiteId;
	}

	public void setWebsiteId(int websiteId) {
		this.websiteId = websiteId;
	}

	public int getLastChapterId() {
		return lastChapterId;
	}

	public void setLastChapterId(int lastChapterId) {
		this.lastChapterId = lastChapterId;
	}

}
