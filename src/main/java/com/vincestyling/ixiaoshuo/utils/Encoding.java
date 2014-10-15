package com.vincestyling.ixiaoshuo.utils;

public enum Encoding {
    GBK("GBK"),
    BIG5("BIG5"),
    UTF8("UTF-8"),
    UTF16BE("UTF-16BE"),
    UTF16LE("UTF-16LE"),
    UNKNOWN("UNKNOWN");

    private Encoding(String name) {
        this.name = name;
        try {
            maxCharLength = "中".getBytes(name).length;
            minCharLength = "\n".getBytes(name).length;
        } catch (Exception e) {
        }
    }

    private String name;

    public String getName() {
        return name;
    }

    private int maxCharLength;

    public int getMaxCharLength() {
        return maxCharLength;
    }

    private int minCharLength;

    public int getMinCharLength() {
        return minCharLength;
    }

}
