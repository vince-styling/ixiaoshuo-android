package com.duowan.mobile.ixiaoshuo.net;

import java.util.Map;

/**
 * a common response object, parse json pattern like that :
 * {"status":200,"message":"","data":{"id":"190"}}
 */
public class Respond {
	private int status;
	private String message;
	private Map<String, Object> data;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

}
