package com.duowan.mobile.ixiaoshuo.net;

import com.duowan.mobile.ixiaoshuo.pojo.Book;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.List;
import java.util.Map;

public class NetUtil {

	public static String convertListToParams(List list, String listName) {
		if (list == null || list.size() == 0) return "";
		StringBuilder params = new StringBuilder();

		for (int i = 0; i < list.size(); i++) {
			ObjectMapper m = new ObjectMapper();
			Map<String, Object> beanMaps = m.convertValue(list.get(0), Map.class);
			for (String key : beanMaps.keySet()) {
				params.append('&').append(listName).append('[').append(i).append(']');
				params.append('.').append(key).append('=').append(beanMaps.get(key));
			}
		}

		return params.toString();
	}

	public static void main(String[] args) throws Exception {
		String json = "{\"status\":200,\"message\":\"\",\"data\":{\"id\":\"109102\",\"name\":\"武动乾坤\",\"websiteId\":\"19\",\"author\":\"不知火舞\",\"category\":\"言情\",\"summary\":\"我是小说简介，有什么问题，大家可以一起来商量下\",\"coverUrl\":\"http://static.zongheng.com/upload/recommend/category/1368694334325.jpg\",\"lastUpdateTime\":\"2013-07-23 16:42:55\",\"updateStatus\":1,\"chapterList\":[{\"id\":123,\"title\":\"我是第一章\"},{\"id\":124,\"title\":\"我是第二章\"}]}}";
		ObjectMapper mapper = GObjectMapper.get();
		Respond resBok = mapper.readValue(json, Respond.class);
		if (resBok.getStatus() == HttpStatus.SC_OK) {
			Book book = mapper.convertValue(resBok.getData(), Book.class);
			System.out.println(book);
		}
	}

}
