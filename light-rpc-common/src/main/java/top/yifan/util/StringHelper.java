package top.yifan.util;

import org.apache.commons.lang3.StringUtils;

/**
 * StringHelper
 */
public class StringHelper {

    private StringHelper() {
    }

    public static String normalize(String value, String nullOrEmptyValue) {
        if (isNullOrWhiteSpace(value)) return nullOrEmptyValue;
        return value;
    }

    public static boolean isNullOrWhiteSpace(String value) {
        if (value == null) return true;
        return StringUtils.isBlank(value);
    }

    public static String clearWhiteSpace(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(" ", "");
    }
}
