package top.yifan.util;

import java.util.Collection;

/**
 * CollectionUtils
 *
 * @author Star Zheng
 */
public class CollectionUtils {

    private CollectionUtils() {

    }
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

}
