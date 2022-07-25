package egovframework.api.admin.utils;

public class StringUtils {
	public static boolean isNumeric(String string) {
		if(string.equals("")){
			return false;
		}
		return string.matches("-?\\d+(\\.\\d+)?");
	}
	
	public static String nvl(String value) {
		return (value != null && value.length() > 0) ? value.trim() : "";
	}
	
	public static String nvl(String value, String replace) {
		return (value != null && value.length() > 0) ? value.trim() : replace;
	}
	
	public static String nvl(Object value) {
		return isNull(value) ? "" : String.valueOf(value);
	}
	
	public static String nvl(Object value, String replace) {
		return isNull(value) ? replace : String.valueOf(value);
	}
	
	public static boolean isNull(Object value) {
		return (value == null) ? true : false;
	}
}
