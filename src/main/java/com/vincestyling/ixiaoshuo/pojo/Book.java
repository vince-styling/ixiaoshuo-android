package com.vincestyling.ixiaoshuo.pojo;

import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.utils.Paths;
import com.vincestyling.ixiaoshuo.utils.StringUtil;

public class Book {
    private int id;
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

    private boolean hasNewChapter = StringUtil.nextRandInt(100) % 2 == 0;
    private String lastUpdateTime;

    private int unreadChapterCount = -1;
    private String lastChapterTitle;

    public String getLocalCoverPath() {
        return Book.getLocalCoverPath(id);
    }

    public static String getLocalCoverPath(int bookId) {
        return Paths.getCoversDirectoryPath() + "book_" + bookId + ".jpg";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getUpdateStatusStr() {
        return updateStatus == STATUS_CONTINUE ?
                R.string.book_status_tip_continue : R.string.book_status_tip_finished;
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

    public boolean isBothType() {
        return isBothType;
    }

    public int getIntBothType() {
        return isBothType ? 1 : 0;
    }

    public void setWasBothType(int wasBothType) {
        this.isBothType = wasBothType == 1;
    }

    public void mockWasBothType() {
        setWasBothType(StringUtil.nextRandInt(100) % 2);
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

    public boolean hasNewChapter() {
        return hasNewChapter;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public int getUnreadChapterCount() {
        return unreadChapterCount;
    }

    public void setUnreadChapterCount(int unreadChapterCount) {
        this.unreadChapterCount = unreadChapterCount;
    }

    public String getLastChapterTitle() {
        return lastChapterTitle;
    }

    public void setLastChapterTitle(String lastChapterTitle) {
        this.lastChapterTitle = lastChapterTitle;
    }
}
