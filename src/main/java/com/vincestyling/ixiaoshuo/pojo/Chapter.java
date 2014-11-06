package com.vincestyling.ixiaoshuo.pojo;

import com.vincestyling.ixiaoshuo.utils.Paths;

import java.io.File;

public class Chapter {
    private int chapterId;                // 章节ID，来自服务器
    private String title;
    private int capacity;

    private int readStatus;
    public static final int READSTATUS_UNREAD = 0;  // 未读
    public static final int READSTATUS_READING = 1;  // 在读
    public static final int READSTATUS_READED = 2;  // 已读

    private int readPosition;

    private int downloadStatus;
    public static final int DOWNLOADSTATUS_REMOTE = 1;  // 本章为远程章节，未下载
    public static final int DOWNLOADSTATUS_NATIVE = 2;  // 本章为本地章节，已下载

    private String filePath;

    public Chapter() {}

    public Chapter(int chapterId, String title) {
        this.chapterId = chapterId;
        this.title = title;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public void setId(int chapterId) {
        this.chapterId = chapterId;
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

    public boolean isReaded() {
        return readStatus == READSTATUS_READED;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public int getReadPosition() {
        return readPosition;
    }

    public void setReadPosition(int readPosition) {
        this.readPosition = readPosition;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void ready(int sourceBookId) {
        filePath = Paths.getCacheDirectorySubFolderPath(sourceBookId) + chapterId;
        downloadStatus = new File(filePath).exists() ? DOWNLOADSTATUS_NATIVE : DOWNLOADSTATUS_REMOTE;
    }

    public boolean isNativeChapter() {
        return downloadStatus == DOWNLOADSTATUS_NATIVE;
    }

    public boolean isRemoteChapter() {
        return downloadStatus == DOWNLOADSTATUS_REMOTE;
    }

    public String getFilePath() {
        return filePath;
    }
}
