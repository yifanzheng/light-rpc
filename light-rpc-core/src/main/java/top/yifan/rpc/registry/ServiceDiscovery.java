package top.yifan.rpc.registry;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * ServiceDiscovery
 *
 * @author Star Zheng
 */
public interface ServiceDiscovery {

    /**
     * 根据服务名称获取服务访问地址
     *
     * @param rpcServiceName rpc 服务地址
     * @return 服务地址
     */
    InetSocketAddress lookup(String rpcServiceName);
}
