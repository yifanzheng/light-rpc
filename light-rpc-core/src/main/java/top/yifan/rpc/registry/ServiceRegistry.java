package top.yifan.rpc.registry;

import top.yifan.extension.SPI;
import top.yifan.rpc.config.ServiceConfig;

import java.net.InetSocketAddress;

/**
 * ServiceRegistry
 *
 * @author Star Zheng
 */
@SPI
public interface ServiceRegistry {

    void register(ServiceConfig serviceConfig, InetSocketAddress socketAddress);

}
