package top.yifan.rpc.loadbalance;

import top.yifan.rpc.domain.Endpoint;
import top.yifan.rpc.exchange.Request;
import top.yifan.rpc.exchange.RequestData;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机负载均衡
 *
 * @author sz7v
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "random";

    @Override
    protected Endpoint doSelect(List<Endpoint> endpoints, RequestData request) {
        int length = endpoints.size();
        return endpoints.get(ThreadLocalRandom.current().nextInt(length));
    }
}
