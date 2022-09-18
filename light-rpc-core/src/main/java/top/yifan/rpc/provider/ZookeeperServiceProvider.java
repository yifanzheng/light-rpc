package top.yifan.rpc.provider;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import top.yifan.exception.RpcException;
import top.yifan.extension.ExtensionLoader;
import top.yifan.rpc.config.ServiceConfig;
import top.yifan.rpc.properties.RpcProperties;
import top.yifan.rpc.registry.ServiceRegistry;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static top.yifan.constants.CommonConstants.RPC_PROTOCOL_PORT;

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
    public void registerService(ServiceConfig serviceConfig) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            this.addService(serviceConfig.getRpcServiceName(), serviceConfig.getService());
            serviceRegistry.register(serviceConfig, new InetSocketAddress(host, getPort()));
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

    private int getPort() {
        String portStr = RpcProperties.getParameter(RPC_PROTOCOL_PORT);
        return StringUtils.isBlank(portStr) ? 8080 : Integer.parseInt(portStr);
    }
}
