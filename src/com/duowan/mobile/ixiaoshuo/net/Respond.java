package com.duowan.mobile.ixiaoshuo.net;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.type.TypeReference;

/**
 * a common response object, parse json pattern like that :
 * {"status":200,"message":"","data":{"id":"190"}}
 */
public class Respond {
	private int status;
	private String message;
	private Object data;		// instances of ArrayList or LinkedHashMap

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

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public static boolean isCorrect(Respond respond) {
		return respond != null && respond.status == HttpStatus.SC_OK;
	}

	public <T> T convert(TypeReference<T> typeRef) {
		return GObjectMapper.get().convertValue(data, typeRef);
	}

	public <T> T convert(Class<T> clazz) {
		return GObjectMapper.get().convertValue(data, clazz);
	}

}
