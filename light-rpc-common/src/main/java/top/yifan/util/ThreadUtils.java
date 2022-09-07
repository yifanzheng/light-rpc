package top.yifan.util;


import top.yifan.exception.ThreadInterruptedException;

/**
 * ThreadUtil
 */
public class ThreadUtils {
    
    private ThreadUtils() {}

    /**
     * 睡眠指定时间，此方法通过将InterruptedException异常转异为非受检的异常，
     * 来避免调用者显示捕获异常，但是如果存在中断线程等操作，那么需要注意处理此类异常
     *
     * @param millis 睡眠时间，单位毫秒
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ThreadInterruptedException(e);
        }
    }
    
}