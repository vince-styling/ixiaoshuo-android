package com.duowan.mobile.ixiaoshuo.net;

import com.duowan.mobile.ixiaoshuo.utils.PaginationList;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.type.TypeReference;

import java.util.Map;

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
		if (clazz == Integer.class || clazz == String.class) {
			Map<String, String> dataMap = (Map<String, String>) data;
			for (String s : dataMap.values()) {
				return (T) s;
			}
		}
		return GObjectMapper.get().convertValue(data, clazz);
	}

	public <T> PaginationList<T> convertPaginationList(Class<T> clazz) {
		return PaginationList.convert((Map<String, Object>) data, clazz);
	}

}
