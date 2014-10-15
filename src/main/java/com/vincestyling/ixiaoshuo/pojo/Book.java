package com.vincestyling.ixiaoshuo.pojo;

import com.vincestyling.ixiaoshuo.utils.Paths;
import com.vincestyling.ixiaoshuo.utils.StringUtil;

public class Book {
    private int bookId;
    private String name;
    private String author;
    private String coverUrl;

    private String summary;

    private long capacity;        // 容量：字数 or 字节数
    private boolean isBothType;

    private String catId;
    private String catName;

    private int readerCount;

    private int updateStatus;

    public static final int STATUS_CONTINUE = 1; // 连载
    public static final int STATUS_FINISHED = 2; // 完结

    private int bookType;
    public static final int TYPE_TEXT = 1; // 文字书籍
    public static final int TYPE_VOICE = 2; // 有声书籍
    public static final int TYPE_LOCAL = 3; // 本地书籍

    private boolean hasNewChapter;
    private int remoteLastChapterId;
    private String lastUpdateTime;

    public String getLocalCoverPath() {
        return Book.getLocalCoverPath(bookId);
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

    public void setId(int id) {
        this.bookId = id;
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

    public void setBothType(boolean isBothType) {
        this.isBothType = isBothType;
    }

    public void setWasBothType(int wasBothType) {
        this.isBothType = wasBothType == 1;
    }

    public long getCapacity() {
        return capacity;
    }

    public String getCapacityStr() {
        return StringUtil.formatWordsCount(capacity);
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
