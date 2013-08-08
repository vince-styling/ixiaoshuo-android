package com.duowan.mobile.ixiaoshuo.pojo;

public class Chapter {
	private int id;				// 章节ID，来自服务器
	private String title;

	private int readStatus;
	public static final int READSTATUS_UNREAD       = 0; // 未读
	public static final int READSTATUS_READING      = 1; // 在读
	public static final int READSTATUS_READ         = 2; // 已读
	private int beginPosition;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setChapterId(int chapterId) {
		this.id = chapterId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getReadStatus() {
		return readStatus;
	}

	public boolean isReading() {
		return readStatus == READSTATUS_READING;
	}

	public boolean isUnRead() {
		return readStatus == READSTATUS_UNREAD;
	}

	public boolean isRead() {
		return readStatus == READSTATUS_READ;
	}

	public void setReadStatus(int readStatus) {
		this.readStatus = readStatus;
	}

	public int getBeginPosition() {
		return beginPosition;
	}

	public void setBeginPosition(int beginPosition) {
		this.beginPosition = beginPosition;
	}

}
