package com.vincestyling.ixiaoshuo.db.statment;

public class UnescapeValue {
    private String originStr;

    private UnescapeValue(String originStr) {
        this.originStr = originStr;
    }

    public static UnescapeValue get(String str) {
        return new UnescapeValue(str);
    }

    @Override
    public String toString() {
        return originStr;
    }
}
