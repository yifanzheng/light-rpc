package top.yifan.rpc.provider;

import lombok.extern.slf4j.Slf4j;
import top.yifan.exception.RpcException;
import top.yifan.extension.ExtensionLoader;
import top.yifan.rpc.config.ServiceConfig;
import top.yifan.rpc.registry.ServiceRegistry;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ZookeeperServiceProvider
 *
 * @author Star Zheng
 */
@Slf4j
public class ZookeeperServiceProvider implements ServiceProvider {

    private static final String NAME = "zookeeper";

    /**
     * key: rpc service name
     * value: service object
     */
    private final Map<String, Object> serviceCacheMap;

    private final ServiceRegistry serviceRegistry;

    public ZookeeperServiceProvider() {
        serviceCacheMap = new ConcurrentHashMap<>();
        serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension(NAME);
    }

    @Override
    public Object getService(String serviceName) {
        Object service = serviceCacheMap.get(serviceName);
        if (Objects.isNull(service)) {
            throw new RpcException("Not found this service");
        }
        return service;
    }

    @Override
    public void publishService(ServiceConfig serviceConfig) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            this.addService(serviceConfig.getRpcServiceName(), serviceConfig.getService());
            serviceRegistry.register(serviceConfig.getRpcServiceName(), new InetSocketAddress(host, 8080));
        } catch (UnknownHostException e) {
            log.error("occur exception when getHostAddress", e);
        }
    }

    private void addService(String rpcServiceName, Object service) {
        if (serviceCacheMap.containsKey(rpcServiceName)) {
            return;
        }
        serviceCacheMap.put(rpcServiceName, service);
        log.info("Add rpc service: {}", rpcServiceName);
    }
}
