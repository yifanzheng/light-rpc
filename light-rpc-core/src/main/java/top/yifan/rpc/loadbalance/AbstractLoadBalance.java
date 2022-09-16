package top.yifan.rpc.loadbalance;

import top.yifan.rpc.domain.Endpoint;
import top.yifan.rpc.exchange.Request;
import top.yifan.util.CollectionUtils;

import java.util.List;

/**
 * @author sz7v
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public Endpoint select(List<Endpoint> endpoints, Request request) {
        if (CollectionUtils.isEmpty(endpoints)) {
            return null;
        }
        if (endpoints.size() == 1) {
            return endpoints.get(0);
        }
        return doSelect(endpoints, request);
    }

    protected abstract Endpoint doSelect(List<Endpoint> endpoints, Request request);
}
