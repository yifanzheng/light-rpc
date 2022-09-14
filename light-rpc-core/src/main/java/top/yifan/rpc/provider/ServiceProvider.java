package top.yifan.rpc.provider;

import top.yifan.rpc.config.ServiceConfig;

/**
 * ServiceProvider
 *
 * @author Star Zheng
 */
public interface ServiceProvider {
    Object getService(String rpcServiceName);

    void publishService(ServiceConfig rpcServiceConfig);
}
