package top.yifan.rpc.loadbalance;

import top.yifan.io.Bytes;
import top.yifan.rpc.domain.Endpoint;
import top.yifan.rpc.exchange.Request;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 一致性哈希负载均衡
 * <p>
 * 借鉴 dubbo 的一致性哈希算法
 *
 * @author sz7v
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "consistenthash";

    private final ConcurrentMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    protected Endpoint doSelect(List<Endpoint> endpoints, Request request) {
        String key = request.getRpcServiceName();
        int endpointsHashCode = System.identityHashCode(endpoints);
        ConsistentHashSelector selector = selectors.get(key);
        if (selector == null || selector.identityHashCode != endpointsHashCode) {
            selectors.put(key, new ConsistentHashSelector(endpoints, 160, endpointsHashCode));
            selector = selectors.get(key);
        }
        return selector.select(key + toKey(request.getParameters()));
    }

    private String toKey(Object[] args) {
        StringBuilder buf = new StringBuilder();
        if (args != null) {
            for (Object arg : args) {
                buf.append(arg);
            }
        }
        return buf.toString();
    }

    private static final class ConsistentHashSelector {

        private final TreeMap<Long, Endpoint> virtualEndpoints;

        private final int identityHashCode;

        ConsistentHashSelector(List<Endpoint> endpoints, int replicaNumber, int identityHashCode) {
            this.virtualEndpoints = new TreeMap<>();
            this.identityHashCode = identityHashCode;
            for (Endpoint endpoint : endpoints) {
                String address = endpoint.getAddress();
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] digest = Bytes.getMD5(address + i);
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest, h);
                        virtualEndpoints.put(m, endpoint);
                    }
                }
            }
        }

        public Endpoint select(String key) {
            byte[] digest = Bytes.getMD5(key);
            return selectForKey(hash(digest, 0));
        }

        private Endpoint selectForKey(long hash) {
            Map.Entry<Long, Endpoint> entry = virtualEndpoints.ceilingEntry(hash);
            if (entry == null) {
                entry = virtualEndpoints.firstEntry();
            }
            return entry.getValue();
        }

        private long hash(byte[] digest, int number) {
            return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                    | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                    | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                    | (digest[number * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }
    }

}
