package com.duowan.mobile.ixiaoshuo.pojo;

import com.duowan.mobile.ixiaoshuo.utils.Paths;
import com.duowan.mobile.ixiaoshuo.utils.StringUtil;

public class Book {
	private int bookId;				// 服务端书籍唯一ID，不变
	private int sourceBookId;		// 服务端书籍源ID，在换源时会更改
	private String name;
	private String author;
	private String coverUrl;

	private String summary;
	private boolean isOriginSummary = true;

	private long capacity;		// 容量：字数 or 字节数
	private boolean isBothType;

	private String catId;
	private String catName;

	private int readerCount;

	private int updateStatus;

	public static final int STATUS_CONTINUE = 1; // 连载
	public static final int STATUS_PAUSE    = 2; // 暂停
	public static final int STATUS_FINISHED = 3; // 完结

	private int bookType;
	public static final int TYPE_TEXT	= 1; // 文字书籍
	public static final int TYPE_VOICE	= 2; // 有声书籍
	public static final int TYPE_LOCAL	= 3; // 本地书籍

	private boolean hasNewChapter;
	private int remoteLastChapterId;
	private String lastUpdateTime;

	public String getLocalCoverPath() {
		return Book.getLocalCoverPath(sourceBookId);
	}

	public static String getLocalCoverPath(int bookId) {
		return Paths.getCoversDirectoryPath() + "book_" + bookId + ".jpg";
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	// TODO : 兼容服务端书籍详情接口的字段名，要让服务端改成bookId
	public void setId(int id) {
		this.bookId = id;
	}

	public int getSourceBookId() {
		return sourceBookId;
	}

	public void setSourceBookId(int sourceBookId) {
		this.sourceBookId = sourceBookId;
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

	public int getReaderCount() {
		return readerCount;
	}

	public void setReaderCount(int readerCount) {
		this.readerCount = readerCount;
	}

	public int getBookType() {
		return bookType;
	}

	public void setBookType(int bookType) {
		this.bookType = bookType;
	}

	// TODO : 服务端应该直接返回数字标识，而不是字符串
	public void setType(String type) {
		if (StringUtil.isEmpty(type)) return;
		if (type.equals("text")) bookType = TYPE_TEXT;
		else if (type.equals("voice")) bookType = TYPE_VOICE;
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
		if (bookType == TYPE_TEXT) return StringUtil.formatWordsCount(capacity);
		return StringUtil.formatSpaceSize(capacity);
	}

	public void setCapacity(long capacity) {
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

	public int getRemoteLastChapterId() {
		return remoteLastChapterId;
	}

	public void setRemoteLastChapterId(int remoteLastChapterId) {
		this.remoteLastChapterId = remoteLastChapterId;
	}

	public boolean hasNewChapter() {
		return hasNewChapter;
	}

	public void setHasNewChapter(int hasNewChapter) {
		this.hasNewChapter = hasNewChapter == 1;
	}

	public int getIntHasNewChapter() {
		return hasNewChapter ? 1 : 0;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

}
