package top.yifan.util;

import top.yifan.io.UnsafeStringWriter;

import java.io.PrintWriter;

/**
 * StringUtils
 *
 * @author Star Zheng
 */
public class StringUtils {

    private StringUtils() {

    }

    public static String toString(Throwable e) {
        UnsafeStringWriter w = new UnsafeStringWriter();
        PrintWriter p = new PrintWriter(w);
        p.print(e.getClass().getName());
        if (e.getMessage() != null) {
            p.print(": " + e.getMessage());
        }
        p.println();
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }

}
