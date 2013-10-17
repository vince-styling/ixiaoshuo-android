package com.duowan.mobile.ixiaoshuo.pojo;

import com.duowan.mobile.ixiaoshuo.utils.Paths;
import com.duowan.mobile.ixiaoshuo.utils.StringUtil;

public class Book {
	private int bid;				// 本地的自增长ID，主键
	private int bookId;				// 服务端的书籍ID，有可能会改变
	private int websiteId;
	private String websiteName;
	private String name;
	private String author;
	private String coverUrl;

	private String summary;
	private boolean isOriginSummary = true;

	private String type;
	private boolean isBothType;

	private long capacity;		// 容量：字数 or 字节数

	private String catId;
	private String catName;

	private String lastUpdateTime;
	private int readerCount;
	private String mLocaPath;

	private int updateStatus;
	public static final int STATUS_CONTINUE = 1; // 连载
    public static final int STATUS_PAUSE    = 2; // 暂停
	public static final int STATUS_FINISHED = 3; // 完结

	public static final String TYPE_TEXT	= "text";		// 文字书籍
	public static final String TYPE_VOICE	= "voice";		// 有声书籍
	public static final String TYPE_LOCAL	= "local";		// 本地书籍

	private int updateChapterCount;

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

	public String getSimpleUpdateStatusStr() {
		return updateStatus == STATUS_CONTINUE ? "连" : "完";
	}

	public boolean isContinue() {
		return updateStatus == STATUS_CONTINUE;
	}

	public boolean isFinished() {
		return updateStatus == STATUS_FINISHED;
	}

	public int getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(int updateStatus) {
		this.updateStatus = updateStatus;
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

	public String getPlainSummary() {
		if (isOriginSummary) {
			summary = StringUtil.trimAsPlainText(summary);
			isOriginSummary = false;
		}
		return summary;
	}

	public String getFormattedSummary() {
		if (isOriginSummary) {
			summary = StringUtil.trimAsFormattedText(summary);
			isOriginSummary = false;
		}
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isBothType() {
		return isBothType;
	}

	public int getIntBothType() {
		return isBothType ? 1 : 0;
	}

	public void setIsBothType(boolean isBothType) {
		this.isBothType = isBothType;
	}

	public void setWasBothType(int wasBothType) {
		this.isBothType = wasBothType == 1;
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

	public int getUpdateChapterCount() {
		return updateChapterCount;
	}

	public void setUpdateChapterCount(int updateChapterCount) {
		this.updateChapterCount = updateChapterCount;
	}

	public String getLocaPath() {
		return mLocaPath;
	}

	public void setLocaPath(String mLocaPath) {
		this.mLocaPath = mLocaPath;
	}

}
