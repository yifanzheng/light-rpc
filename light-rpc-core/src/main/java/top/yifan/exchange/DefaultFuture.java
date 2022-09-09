package top.yifan.exchange;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * DefaultFuture
 *
 * @author Star Zheng
 */
public class DefaultFuture extends CompletableFuture<Object> {

    /**
     * in-flight requests
     */
    private static final Map<String, DefaultFuture> FUTURES = new ConcurrentHashMap<>();

    private DefaultFuture() {
    }

    public static DefaultFuture newFuture(String requestId) {
        DefaultFuture future = new DefaultFuture();
        FUTURES.put(requestId, future);
        return future;
    }

    public static DefaultFuture getFuture(String requestId) {
        return FUTURES.get(requestId);
    }

    public static void sent(String requestId, Object result) {
        DefaultFuture future = FUTURES.remove(requestId);
        if (Objects.nonNull(future)) {
            future.complete(result);
        } else {
            throw new IllegalStateException("Connection lost");
        }
    }

    public static void destroy() {
        FUTURES.clear();
    }
}
