package com.duowan.mobile.ixiaoshuo.net;

import com.duowan.mobile.ixiaoshuo.pojo.Book;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.BasicHttpParams;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.List;
import java.util.Map;

public class NetUtil {

	public static String convertListToParams(List list, String listName) {
		StringBuilder params = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> beanMaps = GObjectMapper.get().convertValue(list.get(i), Map.class);
			for (String key : beanMaps.keySet()) {
				params.append('&').append(listName).append('[').append(i).append(']');
				params.append('.').append(key).append('=').append(beanMaps.get(key));
			}
		}
		return params.toString();
	}

	public static void putListToParams(HttpPost httpPost, List list, String listName) {
		BasicHttpParams params = new BasicHttpParams();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> beanMaps = GObjectMapper.get().convertValue(list.get(i), Map.class);
			for (String key : beanMaps.keySet()) {
				params.setParameter(listName + '[' + i + "]." + key, beanMaps.get(key));
			}
		}
		httpPost.setParams(params);
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
