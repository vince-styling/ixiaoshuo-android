package com.duowan.mobile.ixiaoshuo.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Various String utility functions.
 * Most of the functions herein are re-implementations of the ones in apache
 * commons StringUtils. The reason for re-implementing this is that the
 * functions are fairly simple and using my own implementation saves the
 * inclusion of a 200Kb jar file.
 * @author vince
 */
public class StringUtil {
	
	public static String trim(String text) {
		if(text == null) return "";
		return text.trim();
	}
	
	/** Whether the String is not null, not zero-length and does not contain of only whitespace */
	public static boolean isNotBlank(String text) {
		return !isBlank(text);
	}
	
	/** Whether the String is null, zero-length and does contain only whitespace */
	public static boolean isBlank(String text) {
		if (isEmpty(text)) return true;
		for (int i = 0; i < text.length(); i++) {
			if (!Character.isWhitespace(text.charAt(i))) return false;
		}
		return true;
	}
	
	/** Whether the given string is null or zero-length */
	public static boolean isEmpty(String text) {
		return text == null || text.length() == 0;
	}
	public static boolean isNotEmpty(String text) {
		return !isEmpty(text);
	}
	
	/** Whether the given source string ends with the given suffix, ignoring case. */
	public static boolean endsWithIgnoreCase(String source, String suffix) {
		if (isEmpty(suffix)) return true;
		if (isEmpty(source)) return false;
		if (suffix.length() > source.length()) return false;
		return source.substring(source.length() - suffix.length()).toLowerCase().endsWith(suffix.toLowerCase());
	}
	
	public static String stringArrayToString(String[] array) {
		if(array == null || array.length == 0) return "";
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
	
	/** Gives the substring of the given text before the given separator. */
	public static String substringBefore(String text, char separator) {
		if (isEmpty(text)) return text;
		int sepPos = text.indexOf(separator);
		if (sepPos < 0) return text;
		return text.substring(0, sepPos);
	}
	
	public static int getIntValue(String str) {
		if(str != null && str.length() > 0) {
			try {
				return Integer.parseInt(str);
			} catch (Exception e) {}
		}
		return 0;
	}
	
	public static long getLongValue(String str){
		if(str != null && str.length() > 0) {
			try {
				return  Long.parseLong(str);
			} catch (Exception e) {
			}
		}
		return 0;
	}
	
	public static String removeEmptyChar(String src) {
		if(src == null || src.length() == 0) return src;
		return src.replaceAll("[\r]*[\n]*[　]*[ ]*", "");
	}
	
//	public final static DecimalFormat NO_DECIMAL_POINT_DF = new DecimalFormat("0");
	public final static DecimalFormat TWO_DECIMAL_POINT_DF = new DecimalFormat("0.00");
//	public final static SimpleDateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm");
//	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
//
//	// format of datetime: 2011-08-24 12:22:11, return 08-24 or 12:22
//	public static CharSequence formatDateTime(String datetime) {
//		try {
//			Calendar cal1 = Calendar.getInstance();
//			cal1.setTime(dfDateTime.parse(datetime));
//			Calendar cal2 = Calendar.getInstance();
//			cal2.setTime(new Date());
//			cal1.add(Calendar.HOUR, 1);
//			if (cal1.after(cal2)) {
//				cal1.add(Calendar.HOUR, -1);
//				return ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / 60000) + "分钟前";
//			}
//			cal1.add(Calendar.HOUR, 23);
//			if (cal1.after(cal2)) {
//				cal1.add(Calendar.HOUR, -24);
//				return ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / 3600000) + "小时前";
//			}
//			return datetime.subSequence(0, 10);
//		} catch (Exception ex) {
//			return datetime;
//		}
//	}


	public static boolean isValidUrl(String url) {
		if(isBlank(url)) return false;

		String strPattern = "^(http|https)://(.+)/(.+)([.]+)(.+)$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(url);
		return m.matches();
	}

}
