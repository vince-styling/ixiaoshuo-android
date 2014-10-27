package com.vincestyling.ixiaoshuo.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Various String utility functions.
 * Most of the functions herein are re-implementations of the ones in apache
 * commons StringUtils. The reason for re-implementing this is that the
 * functions are fairly simple and using my own implementation saves the
 * inclusion of a 200Kb jar file.
 *
 * @author vince
 */
public class StringUtil {
    public final static char NEW_LINE_CHAR = '\n';
    public final static String NEW_LINE_STR = "\n";

    public static String trim(String text) {
        if (text == null) return "";
        return text.trim();
    }

    /**
     * Whether the String is not null, not zero-length and does not contain of only whitespace
     */
    public static boolean isNotBlank(String text) {
        return !isBlank(text);
    }

    /**
     * Whether the String is null, zero-length and does contain only whitespace
     */
    public static boolean isBlank(String text) {
        if (isEmpty(text)) return true;
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i))) return false;
        }
        return true;
    }

    /**
     * Whether the given string is null or zero-length
     */
    public static boolean isEmpty(String text) {
        return TextUtils.isEmpty(text);
    }

    public static boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }

    /**
     * Whether the given source string ends with the given suffix, ignoring case.
     */
    public static boolean endsWithIgnoreCase(String source, String suffix) {
        if (isEmpty(suffix)) return true;
        if (isEmpty(source)) return false;
        if (suffix.length() > source.length()) return false;
        return source.substring(source.length() - suffix.length()).toLowerCase().endsWith(suffix.toLowerCase());
    }

    public static String stringArrayToString(String[] array) {
        if (array == null || array.length == 0) return "";
        StringBuilder string = new StringBuilder();
        for (String item : array) {
            string.append(item).append(',');
        }
        return string.toString();
    }

//	public static String intToLessthanLength(int val, int wide) {
//		String res = String.valueOf(val);
//		if(res.length() >= wide) return res;
//
//		StringBuilder sb = new StringBuilder(res);
//		for(int i = 0; i < wide - res.length(); i++) sb.insert(0, '0');
//		return sb.toString();
//	}

    /**
     * Gives the substring of the given text before the given separator.
     */
    public static String substringBefore(String text, char separator) {
        if (isEmpty(text)) return text;
        int sepPos = text.indexOf(separator);
        if (sepPos < 0) return text;
        return text.substring(0, sepPos);
    }

    public static int getIntValue(String str) {
        if (str != null && str.length() > 0) {
            try {
                return Integer.parseInt(str);
            } catch (Exception e) {
            }
        }
        return 0;
    }

    public static long getLongValue(String str) {
        if (str != null && str.length() > 0) {
            try {
                return Long.parseLong(str);
            } catch (Exception e) {
            }
        }
        return 0;
    }

    public static String removeEmptyChar(String src) {
        if (src == null || src.length() == 0) return src;
        return src.replaceAll("[\r]*[\n]*[　]*[ ]*", "");
    }

    public final static DecimalFormat NO_DECIMAL_POINT_DF = new DecimalFormat("0");
    public final static DecimalFormat ONE_DECIMAL_POINT_DF = new DecimalFormat("0.0");
    public final static DecimalFormat TWO_DECIMAL_POINT_DF = new DecimalFormat("0.00");
    public final static SimpleDateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm");
//	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static CharSequence getDiffWithNow(String datetime) {
        try {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(dfDateTime.parse(datetime));

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(new Date());

            cal1.add(Calendar.HOUR, 1);
            if (cal1.after(cal2)) {
                cal1.add(Calendar.HOUR, -1);
                long diffInSecond = (cal2.getTimeInMillis() - cal1.getTimeInMillis()) / 1000;
                long diffInMinute = diffInSecond / 60;
                return diffInMinute == 0 ? diffInSecond + "秒前" : diffInMinute + "分钟前";
            }

            cal1.add(Calendar.HOUR, 23);
            if (cal1.after(cal2)) {
                cal1.add(Calendar.HOUR, -24);
                return ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / 3600000) + "小时前";
            }

            cal1.add(Calendar.DAY_OF_YEAR, 30);
            if (cal1.after(cal2)) {
                cal1.add(Calendar.DAY_OF_YEAR, -31);
                return ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / 86400000) + "天前";
            }

            return datetime.subSequence(5, 10);
        } catch (Exception ex) {
        }
        return "";
    }

    public static String formatSpaceSize(long file_size) {
        if (file_size < 1024) { // 不足1K
            return file_size + "B";
        } else if (file_size < 1024 * 1024) { // 不足1M
            return NO_DECIMAL_POINT_DF.format((double) file_size / 1024) + "KB";
        } else if (file_size < 1024 * 1024 * 1024) { // 不足1G
            return NO_DECIMAL_POINT_DF.format((double) file_size / 1024 / 1024) + "MB";
        } else {
            return ONE_DECIMAL_POINT_DF.format((double) file_size / 1024 / 1024 / 1024) + "GB";
        }
    }

    public static String formatWordsCount(long capacity) {
        String countStr;
        if (capacity < 1000) {
            countStr = String.valueOf(capacity);
        } else if (capacity < 10000) {
            countStr = (capacity / 1000) + "千";
        } else {
            countStr = ONE_DECIMAL_POINT_DF.format((double) capacity / 10000) + '万';
        }
        return countStr + '字';
    }

    public static boolean isValidUrl(String url) {
        if (isBlank(url)) return false;

        String strPattern = "^(http|https)://(.+)/(.+)([.]+)(.+)$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(url);
        return m.matches();
    }

    private static final Random GLOBAL_RANDOM = new Random();

    public static int nextRandInt(int max) {
        GLOBAL_RANDOM.setSeed(System.currentTimeMillis());
        return GLOBAL_RANDOM.nextInt(max);
    }

    public static int nextRandInt(int min, int max) {
        return min + nextRandInt(max - min);
    }

    public static void main(String[] args) throws Exception {
//		System.out.println(getDiffWithNow("2013-09-02 20:02:24"));
        System.out.println(formatWordsCount(8090000000l));
        System.out.println(formatSpaceSize(100));
    }

}
