package com.vincestyling.ixiaoshuo.utils;

import com.vincestyling.ixiaoshuo.net.GObjectMapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaginationList<T> extends ArrayList<T> {
    private int totalPageCount;
    private int totalItemCount;
    private int pageItemCount;
    private int curPageNo;

    public PaginationList() {
        super(0);
    }

    public PaginationList(List<T> ts, int pageNo, int pageItemCount, int totalItemCount) {
        setPagination(pageNo, pageItemCount, totalItemCount);
        addAll(ts);
    }

    public void setPagination(int pageNo, int pageItemCount, int totalItemCount) {
        this.curPageNo = pageNo;
        this.pageItemCount = pageItemCount;
        this.totalItemCount = totalItemCount;
        this.totalPageCount = (totalItemCount - 1) / pageItemCount + 1;
    }

    public int getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(int totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    public int getTotalItemCount() {
        return totalItemCount;
    }

    public void setTotalItemCount(int totalItemCount) {
        this.totalItemCount = totalItemCount;
    }

    public int getPageItemCount() {
        return pageItemCount;
    }

    public void setPageItemCount(int pageItemCount) {
        this.pageItemCount = pageItemCount;
    }

    public int getCurPageNo() {
        return curPageNo;
    }

    public void setCurPageNo(int curPageNo) {
        this.curPageNo = curPageNo;
    }

    public boolean hasNextPage() {
        return curPageNo < totalPageCount;
    }

    private static final String methodPrefix = "set";

    public static <T> PaginationList<T> convert(Map<String, Object> dataMap, Class<T> clazz) {
        try {
            PaginationList<T> list = new PaginationList<T>();

            for (String key : dataMap.keySet()) {
                Object value = dataMap.get(key);
                if (value instanceof List) {
                    List datas = (List) value;
                    list.ensureCapacity(datas.size());
                    for (Object item : datas) {
                        list.add(GObjectMapper.get().convertValue(item, clazz));
                    }
                } else {
                    for (Method method : PaginationList.class.getMethods()) {
                        if (method.getName().equalsIgnoreCase(methodPrefix + key)) {
                            method.invoke(list, Integer.parseInt(dataMap.get(key).toString()));
                            break;
                        }
                    }
                }
            }

            return list;
        } catch (Exception ex) {
        }
        return null;
    }

}
