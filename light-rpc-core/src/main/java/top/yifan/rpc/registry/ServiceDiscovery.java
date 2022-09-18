package top.yifan.rpc.registry;

import top.yifan.extension.SPI;
import top.yifan.rpc.domain.Endpoint;
import top.yifan.rpc.exchange.Request;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * ServiceDiscovery
 *
 * @author Star Zheng
 */
@SPI
public interface ServiceDiscovery {

    /**
     * 根据服务名称获取服务访问地址
     *
     * @param request request
     * @return 服务地址
     */
    Endpoint lookup(Request request);
}
