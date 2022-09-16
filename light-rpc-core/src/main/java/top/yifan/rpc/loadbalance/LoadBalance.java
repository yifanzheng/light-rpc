package top.yifan.rpc.loadbalance;

import top.yifan.extension.SPI;
import top.yifan.rpc.exchange.Request;

import java.util.List;

/**
 * LoadBalance
 *
 * @author Star Zheng
 */
@SPI
public interface LoadBalance {

    Endpoint select(List<Endpoint> endpoints, Request request);

}
