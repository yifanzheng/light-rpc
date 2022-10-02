package top.yifan.rpc.exchange;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DefaultFuture
 *
 * @author Star Zheng
 */
public class DefaultExchangeFuture extends CompletableFuture<Object> {

    /**
     * in-flight requests
     */
    private static final Map<String, DefaultExchangeFuture> FUTURES = new ConcurrentHashMap<>();

    private DefaultExchangeFuture() {
    }

    public static DefaultExchangeFuture newFuture(long messageId) {
        DefaultExchangeFuture future = new DefaultExchangeFuture();
        FUTURES.put(String.valueOf(messageId), future);
        return future;
    }

    public static DefaultExchangeFuture getFuture(String requestId) {
        return FUTURES.get(requestId);
    }

    public static void sent(long msgId, Object result) {
        DefaultExchangeFuture future = FUTURES.remove(String.valueOf(msgId));
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
