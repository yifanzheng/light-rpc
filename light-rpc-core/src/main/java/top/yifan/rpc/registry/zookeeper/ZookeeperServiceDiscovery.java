package top.yifan.rpc.registry.zookeeper;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import top.yifan.rpc.registry.ServiceDiscovery;
import top.yifan.rpc.registry.zookeeper.client.ZookeeperTemplate;
import top.yifan.util.URLUtil;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static top.yifan.constants.CommonConstants.ZK_ROOT;

/**
 * ZookeeperServiceDiscovery
 *
 * @author Star Zheng
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery {

    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();

    private final ZookeeperTemplate zookeeperTemplate;

    public ZookeeperServiceDiscovery() {
        this.zookeeperTemplate = ZookeeperTransporter.getInstance().connect("");
    }

    @Override
    public InetSocketAddress lookup(String rpcServiceName) {
        List<String> endpoints = listServiceEndpoints(rpcServiceName);
        // TODO loadbalance

        return null;
    }

    private List<String> listServiceEndpoints(String rpcServiceName) {
        String lock = ZKServiceDiscoveryLock.buildLock(rpcServiceName);
        synchronized (lock) {
            if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
                return SERVICE_ADDRESS_MAP.get(rpcServiceName);
            }
            // 类似：/rpc/top.yifan.service.DemoService/127.0.0.1:8080
            String serviceNodePath = URLUtil.fullURL(ZK_ROOT, rpcServiceName);
            List<String> serviceEndpoints = zookeeperTemplate.getChildren(serviceNodePath);
            // 注册服务监听器
            registerNodeWacher(serviceNodePath, rpcServiceName);
            return serviceEndpoints;
        }
    }

    private void registerNodeWacher(String serviceNodePath, String rpcServiceName) {
        try {
            zookeeperTemplate.watchChildrenForNodePath(serviceNodePath, (serviceEndpoints) -> {
                SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceEndpoints);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
