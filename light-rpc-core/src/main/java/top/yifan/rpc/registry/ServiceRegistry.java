package top.yifan.rpc.registry;

import java.net.InetSocketAddress;

/**
 * ServiceRegistry
 *
 * @author Star Zheng
 */
public interface ServiceRegistry {

    void register(String rpcServiceName, InetSocketAddress socketAddress);

}
