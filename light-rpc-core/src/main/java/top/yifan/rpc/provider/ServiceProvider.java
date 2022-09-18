package top.yifan.rpc.provider;

import top.yifan.extension.SPI;
import top.yifan.rpc.config.ServiceConfig;

/**
 * ServiceProvider
 *
 * @author Star Zheng
 */
@SPI
public interface ServiceProvider {

    Object getService(String rpcServiceName);

    void registerService(ServiceConfig rpcServiceConfig);
}
