package com.vincestyling.ixiaoshuo.pojo;

public class BookOnUpdate {
	int bookId, lastChapterId, type;

	public BookOnUpdate() {}
	public BookOnUpdate(int bookId, int lastChapterId, int type) {
		this.type = type;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
