package com.duowan.mobile.ixiaoshuo.pojo;


public class Chapter {
	private int id;				// 章节ID，来自服务器
	private String title;
	private int capacity;

	private int readStatus;
	public static final int READSTATUS_UNREAD       = 0; // 未读
	public static final int READSTATUS_READING      = 1; // 在读
	public static final int READSTATUS_READED 		= 2; // 已读
	private int beginPosition;
	
	private String voiceUrl; //有声小说中章节对应的 流媒体地址，文字小说为null
	
	private int mDownloadId = -1; //有声小说中章节对应的 流媒体文件下载时产生的 下载任务id,任务取消，再次下载的时候，会重新赋予任id
								  //只有在进行中的任务才不为 -1
	

	public Chapter() {}

	public Chapter(int id, String title) {
		this.id = id;
		this.title = title;
	}

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
		return readStatus == READSTATUS_READED;
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

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getVoiceUrl() {
		return voiceUrl;
	}

	public void setVoiceUrl(String voiceUrl) {
		this.voiceUrl = voiceUrl;
	}

	public int getDownloadId() {
		return mDownloadId;
	}

	public void setDownloadId(int mDownloadId) {
		this.mDownloadId = mDownloadId;
	}


}
