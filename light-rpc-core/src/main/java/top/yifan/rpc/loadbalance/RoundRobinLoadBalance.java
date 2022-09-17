package top.yifan.rpc.loadbalance;

import top.yifan.rpc.domain.Endpoint;
import top.yifan.rpc.exchange.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 轮询负载均衡，支持权重
 * <p>
 * 借鉴 weibo motan 的基于权重的轮询负载算法：https://github.com/weibocom/motan/blob/master/motan-core/src/main/java/com/weibo/api/motan/cluster/loadbalance/ConfigurableWeightLoadBalance.java
 *
 * @author sz7v
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "roundrobin";

    private final ConcurrentMap<String, RoundRobinSelector> selectors = new ConcurrentHashMap<>();

    @Override
    protected Endpoint doSelect(List<Endpoint> endpoints, Request request) {
        String key = request.getRpcServiceName();
        int endpointsHashCode = System.identityHashCode(endpoints);
        RoundRobinSelector selector = selectors.get(key);
        if (selector == null || selector.identityHashCode != endpointsHashCode) {
            selectors.put(key, new RoundRobinSelector(endpoints, endpointsHashCode));
            selector = selectors.get(key);
        }
        return selector.select();
    }


    private static final class RoundRobinSelector {

        private final int identityHashCode;

        private final int randomKeySize;
        private final List<Endpoint> randomKeyList = new ArrayList<>();
        private final AtomicInteger cursor = new AtomicInteger(0);

        RoundRobinSelector(List<Endpoint> endpoints, int identityHashCode) {
            this.identityHashCode = identityHashCode;
            List<Integer> weightsArr = endpoints.stream()
                    .map(Endpoint::getWeight)
                    .collect(Collectors.toList());
            // 求出最大公约数，若不为1，对权重做除法
            int weightGcd = findGcd(weightsArr.toArray(new Integer[]{}));
            if (weightGcd != 1) {
                for (Endpoint endpoint : endpoints) {
                    endpoint.setWeight(endpoint.getWeight() / weightGcd);
                }
            }
            for (Endpoint endpoint : endpoints) {
                for (int i = 0; i < endpoint.getWeight(); i++) {
                    randomKeyList.add(endpoint);
                }
            }
            Collections.shuffle(randomKeyList);
            randomKeySize = randomKeyList.size();
        }

        public Endpoint select() {
            return randomKeyList.get(Math.abs(cursor.getAndAdd(1)) % randomKeySize);
        }

        /**
         * 求最大公约数
         */
        private int findGcd(int n, int m) {
            return (n == 0 || m == 0) ? n + m : findGcd(m, n % m);
        }

        /**
         * 求最大公约数
         */
        private int findGcd(Integer[] arr) {
            if (arr.length == 1) return arr[0];
            int i = 0;
            for (; i < arr.length - 1; i++) {
                arr[i + 1] = findGcd(arr[i], arr[i + 1]);
            }
            return findGcd(arr[i], arr[i - 1]);
        }
    }

}
