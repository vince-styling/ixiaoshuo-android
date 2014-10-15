package com.vincestyling.ixiaoshuo.pojo;

public class BookByLocation {
    private int id;
    private String name;
    private int updateStatus;
    private int distance;
    private int memberId;
    private boolean isBothType;
    private boolean isTextBook;

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

    public int getUpdateStatus() {
        return updateStatus;
    }

    public boolean isFinished() {
        return updateStatus == Book.STATUS_FINISHED;
    }

    public void setUpdateStatus(int updateStatus) {
        this.updateStatus = updateStatus;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public boolean isBothType() {
        return isBothType;
    }

    public void setBothType(boolean isBothType) {
        this.isBothType = isBothType;
    }

    public boolean isTextBook() {
        return isTextBook;
    }

    public void setTextBook(boolean isTextBook) {
        this.isTextBook = isTextBook;
    }
}