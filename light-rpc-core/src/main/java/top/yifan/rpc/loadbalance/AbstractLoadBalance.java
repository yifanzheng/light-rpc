package top.yifan.rpc.loadbalance;

import top.yifan.rpc.exchange.Request;

import java.util.List;

/**
 * @author sz7v
 */
public class AbstractLoadBalance implements LoadBalance {


    @Override
    public Endpoint select(List<Endpoint> endpoints, Request request) {
        return null;
    }

    protected abstract Endpoint doSelect();
}
