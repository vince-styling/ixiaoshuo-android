package com.duowan.mobile.ixiaoshuo.pojo;

import com.duowan.mobile.ixiaoshuo.utils.Paths;
import com.duowan.mobile.ixiaoshuo.utils.StringUtil;

import java.util.List;

public class Book {
	private int bid;				// 本地的自增长ID，主键
	private int bookId;				// 服务端的书籍ID，有可能会改变
	private int websiteId;
	private String websiteName;
	private String name;
	private String author;
	private String coverUrl;
	private String summary;

	// TODO : 所有返回列表的接口，记得同时返回type！！！！！
	private String type = TYPE_TEXT;
	private boolean isBothType;

	private long capacity;		// 容量：字数 or 字节数

	private String catId;
	private String catName;

	private String lastUpdateTime;
	private int readerCount;

	private int updateStatus;
	public static final int STATUS_CONTINUE = 1; // 连载
	public static final int STATUS_FINISHED = 3; // 完结

	public static final String TYPE_TEXT	= "text";		// 文字书籍
	public static final String TYPE_VOICE	= "voice";		// 有声书籍
	public static final String TYPE_LOCAL	= "local";		// 本地书籍

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
		return updateStatus == STATUS_CONTINUE ? "连载" : "完结";
	}

	public String getUpdateStatusSimpleStr() {
		return updateStatus == STATUS_CONTINUE ? "连" : "完";
	}

	public int getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(int updateStatus) {
		this.updateStatus = updateStatus;
	}

	// TODO : 临时方法，要记得找接口开发人把命名改回来！！！！
	public void setUpdate_status(int update_status) {
		this.updateStatus = update_status;
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
		this.summary = StringUtil.trimEmpty(summary);
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isBothType() {
		return isBothType;
	}

	public void setIsBothType(boolean isBothType) {
		this.isBothType = isBothType;
	}

	public long getCapacity() {
		return capacity;
	}

	public String getCapacityStr() {
		if (type.equals(TYPE_TEXT)) return StringUtil.formatWordsCount(capacity);
		return StringUtil.formatSpaceSize(capacity);
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getCatId() {
		return catId;
	}

	public void setCatId(String catId) {
		this.catId = catId;
	}

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

}
