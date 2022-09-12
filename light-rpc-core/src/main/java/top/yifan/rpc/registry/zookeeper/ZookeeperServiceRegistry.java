package top.yifan.rpc.registry.zookeeper;

import top.yifan.rpc.registry.ServiceRegistry;

import java.net.InetSocketAddress;

/**
 * ZookeeperServiceRegistry
 *
 * @author Star Zheng
 */
public class ZookeeperServiceRegistry implements ServiceRegistry {

    @Override
    public void register(String rpcServiceName, InetSocketAddress socketAddress) {

    }
}
