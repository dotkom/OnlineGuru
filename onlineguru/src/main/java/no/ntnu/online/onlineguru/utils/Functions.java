package no.ntnu.online.onlineguru.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Functions {
	public static boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch(NumberFormatException nfe) {
			return false;
		}
	}
	
	public static String cleanString(String str) {
		str = str.replaceAll("\\s+", " ");
		return str;
	}
	
	public static boolean isLong(String strLong) {
		try {
			Long.parseLong(strLong);
			return true;
		} catch(IllegalArgumentException e) {
			return false;
		}
	}
	
	public static String getClassName(Object object) {
		String[] classNameParts = object.getClass().getName().split("\\.");
		return classNameParts[classNameParts.length-1];
	}
	
	public static boolean validDate(String date) {
		try {
			Date.parse(date);
			return true;
		} catch(IllegalArgumentException e) {
			return false;
		}
	}
	
	public static String getProperTimeFormat(long millis) {
		Date date = new Date(millis);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		return sdf.format(date).toString();
	}
	
	public static String getFormatedCurrentTime() {
		return getProperTimeFormat(System.currentTimeMillis());
		
	}
	
	public static String removeChar(String s, char c) {
		String r = "";
		for (int i = 0; i < s.length(); i ++) {
			if (s.charAt(i) != c) r += s.charAt(i);
		}
		return r;
	}
	
	public static String stripHTMLEntities(String text) {
		return DecodeHtmlEntities.stripHTMLEntities(text);
	}
	
}
