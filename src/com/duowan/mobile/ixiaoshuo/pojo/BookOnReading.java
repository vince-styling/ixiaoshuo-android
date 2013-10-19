package com.duowan.mobile.ixiaoshuo.pojo;

import com.duowan.mobile.ixiaoshuo.db.AppDAO;

public class BookOnReading extends Book {
	// TODO : 检查阅读时对chapterCount、chapterIndex的使用情况，这两个字段应该不用每次都查数据库
	private int chapterCount;
	private int chapterIndex;    // start from zero
	private Chapter chapter;
	// temporaryFlag 改为 is_on_bookshelf，并检查相关逻辑
	private boolean temporaryFlag;    // 是否为暂存书籍，即：用户未执行添加到书架操作

	public BookOnReading() {
	}

	public void setChapter(Chapter chapter) {
		this.chapter = chapter;
		chapterIndex = AppDAO.get().getBookChapterIndex(getBookId(), getChapterId());
	}

	public Chapter getChapter() {
		return chapter;
	}

	public int getChapterId() {
		return chapter != null ? chapter.getChapterId() : 0;
	}

	public String getChapterTitle() {
		return chapter != null ? chapter.getTitle() : "";
	}

	public boolean hasChapters() {
		return chapter != null;
	}

	public boolean hasNextChapter() {
		return chapterIndex + 1 < chapterCount;
	}

	public boolean hasPreviousChapter() {
		return chapterIndex > 0;
	}

	public int getChapterCount() {
		return chapterCount;
	}

	public void setChapterCount(int chapterCount) {
		this.chapterCount = chapterCount;
	}

	public int getRemainChapterCount() {
		return chapterCount - chapterIndex - 1;
	}

	public int getChapterIndex() {
		return chapterIndex;
	}

	public void setChapterIndex(int chapterIndex) {
		this.chapterIndex = chapterIndex;
	}

	public void setTemporaryFlag(int temporaryFlag) {
		this.temporaryFlag = temporaryFlag == 1;
	}

	public boolean isTemporary() {
		return temporaryFlag;
	}

}
