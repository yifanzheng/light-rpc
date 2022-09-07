package top.yifan.constants;

/**
 * CommonConstants
 *
 * @author Star Zheng
 */
public class CommonConstants {

    public static final String OS_NAME_KEY = "os.name";
    public static final String OS_LINUX_PREFIX = "linux";
    public static final String NETTY_EPOLL_ENABLE_KEY = "netty.epoll.enable";
    public static final int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 16);
}
