package com.vincestyling.ixiaoshuo.pojo;

public class BookByLocation {
	private int id;
	private String name;
	private int updateStatus;
	private int distance;
	private int memberId;

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
}