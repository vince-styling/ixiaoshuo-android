package com.duowan.mobile.ixiaoshuo.pojo;

import com.duowan.mobile.ixiaoshuo.utils.Paths;

import java.util.List;

public class Book {
	private int bid;				// 本地的自增长ID，主键
	private int bookId;				// 服务端的书籍ID，有可能会改变
	private int websiteId;
	private String websiteName;
	private String name;
	private String author;
	private String category;
	private String coverUrl;
	private String summary;
	private String lastUpdateTime;
	private int readerCount;

	private int updateStatus;
	public static final int STATUS_CONTINUE = 1; // 连载
	public static final int STATUS_FINISHED = 3; // 完结

	public static final String RANK_WEEK	= "week";
	public static final String RANK_MONTH	= "month";
	public static final String RANK_TOTAL	= "total";

	private List<Chapter> chapterList;

	public boolean hasChapters() {
		return chapterList != null && chapterList.size() > 0;
	}

	public Chapter getReadingChapter() {
		for (Chapter chapter : chapterList) {
			if (chapter.isReading()) return chapter;
		}
		return chapterList.get(0);
	}

	public Chapter getNextChapter() {
		int index = getReadChapterIndex();
		return ++index < chapterList.size() ? chapterList.get(index) : null;
	}

	public Chapter getPreviousChapter() {
		int index = getReadChapterIndex();
		return index > 0 ? chapterList.get(--index) : null;
	}

	public int getReadChapterIndex() {
		return chapterList.indexOf(getReadingChapter());
	}

	public int getChapterCount() {
		return chapterList.size();
	}

	public List<Chapter> getChapterList() {
		return chapterList;
	}

	public void setChapterList(List<Chapter> chapterList) {
		this.chapterList = chapterList;
	}

	public void makeChapterReading(Chapter chapter) {
		if (chapter.isReading()) return;
		for (Chapter chapt : chapterList) {
			if (chapt.isReading()) {
				chapt.setReadStatus(Chapter.READSTATUS_READ);
				chapt.setBeginPosition(0);
			}
		}
		chapter.setReadStatus(Chapter.READSTATUS_READING);
	}

	public String getLocalCoverPath() {
		return Book.getLocalCoverPath(bookId);
	}

	public static String getLocalCoverPath(int bookId) {
		return Paths.getCoversDirectoryPath() + "book_" + bookId + ".jpg";
	}

	public int getBid() {
		return bid;
	}

	public void setBid(int bid) {
		this.bid = bid;
	}

	public int getBookId() {
		return bookId;
	}

	public void setId(int id) {
		this.bookId = id;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getUpdateStatusStr() {
		return updateStatus == 1 ? "连载" : "完结";
	}

	public int getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(int updateStatus) {
		this.updateStatus = updateStatus;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getWebsiteName() {
		return websiteName;
	}

	public void setWebsiteName(String websiteName) {
		this.websiteName = websiteName;
	}

	public int getWebsiteId() {
		return websiteId;
	}

	public void setWebsiteId(int websiteId) {
		this.websiteId = websiteId;
	}

	public int getReaderCount() {
		return readerCount;
	}

	public void setReaderCount(int readerCount) {
		this.readerCount = readerCount;
	}

}
