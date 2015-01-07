package com.vincestyling.ixiaoshuo.net;

import com.vincestyling.asqliteplus.DBOverseer;
import com.vincestyling.asqliteplus.PaginationList;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.type.TypeReference;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * a common response object, parse json pattern like that :
 * {"status":200,"message":"","data":{"id":"190"}}
 */
public class Respond {
    private int status;
    private String message;
    private Object data;        // instances of ArrayList or LinkedHashMap

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
        return convert((Map<String, Object>) data, clazz);
    }

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
                        if (method.getName().equalsIgnoreCase(DBOverseer.METHOD_PREFIX + key)) {
                            method.invoke(list, Integer.parseInt(dataMap.get(key).toString()));
                            break;
                        }
                    }
                }
            }

            return list;
        } catch (Exception ex) {}
        return null;
    }

    @Override
    public String toString() {
        return "Respond{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

}
