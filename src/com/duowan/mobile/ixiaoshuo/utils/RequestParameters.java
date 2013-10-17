package com.duowan.mobile.ixiaoshuo.utils;

import java.util.ArrayList;
import android.text.TextUtils;


/**
 * 请求参数类
 * @author gaocong@yy.com
 */

public class RequestParameters {
	
	//以下为http请求地址
	public static final String RADAR_SEARCH = "/book/list_bylocation.do";
	public static final String BOOK_SEARCH = "/book/search.do";
	public static final String GET_MEMBER_INFO ="/get_memberinfo.do";
	public static final String GET_VOICE_CHAPLIST = "/book_voice/chapterlist.do";
	
	//搜索接口用
	public static final String SEARCH_KEY_WORD = "keyword";
	public static final String TYPE = "type";
	public static final String PAGENO = "pageNo";
	public static final String PAGE_ITEM_COUNT = "pageItemCount";
	
	//雷达接口用
	public static final String LONGITUDE = "longitude";
	public static final String LATITUDE = "latitude";
	public static final String MEMBERID = "memberId";
	public static final String PAGE_NO = "pageNo";
	
	//获取用户信息
	public static final String IMEI = "imei";
	public static final String SYS_VERSION = "systemVersion";
	public static final String DEVICE_MODE = "deviceMode";
	
	
	private ArrayList<String> mKeys = new ArrayList<String>();
	private ArrayList<String> mValues=new ArrayList<String>();
	
	public RequestParameters(){
		
	}
	
	
	public void add(String key, String value){
	    if(!TextUtils.isEmpty(key)&&!TextUtils.isEmpty(value)){
	        this.mKeys.add(key);
	        mValues.add(value);
	    }
	   
	}
	
	public void add(String key, int value){
	    this.mKeys.add(key);
        this.mValues.add(String.valueOf(value));
	}
	public void add(String key, long value){
	    this.mKeys.add(key);
        this.mValues.add(String.valueOf(value));
    }
	
	public void remove(String key){
	    int firstIndex=mKeys.indexOf(key);
	    if(firstIndex>=0){
	        this.mKeys.remove(firstIndex);
	        this.mValues.remove(firstIndex);
	    }
	  
	}
	
	public void remove(int i){
	    if(i<mKeys.size()){
	        mKeys.remove(i);
	        this.mValues.remove(i);
	    }
		
	}
	
	
	private int getLocation(String key){
		if(this.mKeys.contains(key)){
			return this.mKeys.indexOf(key);
		}
		return -1;
	}
	
	public String getKey(int location){
		if(location >= 0 && location < this.mKeys.size()){
			return this.mKeys.get(location);
		}
		return "";
	}
	
	
	public String getValue(String key){
	    int index=getLocation(key);
	    if(index>=0 && index < this.mKeys.size()){
	        return  this.mValues.get(index);
	    }
	    else{
	        return null;
	    }
		
		
	}
	
	public String getValue(int location){
	    if(location>=0 && location < this.mKeys.size()){
	        String rlt = this.mValues.get(location);
	        return rlt;
	    }
	    else{
	        return null;
	    }
		
	}
	
	
	public int size(){
		return mKeys.size();
	}
	
	public void addAll(RequestParameters parameters){
		for(int i = 0; i < parameters.size(); i++){
			this.add(parameters.getKey(i), parameters.getValue(i));
		}
		
	}
	
	public void clear(){
		this.mKeys.clear();
		this.mValues.clear();
	}
	
	
}
