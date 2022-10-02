package top.yifan.rpc.loadbalance;

import top.yifan.extension.SPI;
import top.yifan.rpc.domain.Endpoint;
import top.yifan.rpc.exchange.Request;
import top.yifan.rpc.exchange.RequestData;

import java.util.List;

/**
 * LoadBalance
 *
 * @author Star Zheng
 */
@SPI(RandomLoadBalance.NAME)
public interface LoadBalance {

    Endpoint select(List<Endpoint> endpoints, RequestData request);

}
