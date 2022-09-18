package top.yifan.rpc.registry.zookeeper;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import top.yifan.exception.RpcException;
import top.yifan.extension.ExtensionLoader;
import top.yifan.rpc.domain.Endpoint;
import top.yifan.rpc.exchange.Request;
import top.yifan.rpc.loadbalance.LoadBalance;
import top.yifan.rpc.properties.RpcProperties;
import top.yifan.rpc.registry.ServiceDiscovery;
import top.yifan.rpc.registry.zookeeper.client.ZookeeperTemplate;
import top.yifan.util.CollectionUtils;
import top.yifan.util.URLUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static top.yifan.constants.CommonConstants.*;

/**
 * ZookeeperServiceDiscovery
 *
 * @author Star Zheng
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery {

    private static final Map<String, List<Endpoint>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();

    private final ZookeeperTemplate zookeeperTemplate;

    private final LoadBalance loadBalance;

    public ZookeeperServiceDiscovery() {
        // 初始化Zookeeper连接
        String zkAddress = RpcProperties.getParameter(SUBSCRIBE_ADDRESS_KEY);
        this.zookeeperTemplate = ZookeeperTransporter.getInstance().connect(zkAddress);
        // 初始化loadBalance
        String loadBalanceName = RpcProperties.getParameter(LOADBALANCE_STRATEGY_KEY);
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getOrDefaultExtension(loadBalanceName);
    }

    @Override
    public Endpoint lookup(Request request) {
        List<Endpoint> endpoints = listServiceEndpoints(request.getRpcServiceName());
        if (CollectionUtils.isEmpty(endpoints)) {
            throw new RpcException("Not found specified service");
        }
        // load balance
        return loadBalance.select(endpoints, request);
    }

    private List<Endpoint> listServiceEndpoints(String rpcServiceName) {
        String lock = ZKServiceDiscoveryLock.buildLock(rpcServiceName);
        synchronized (lock) {
            if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
                return SERVICE_ADDRESS_MAP.get(rpcServiceName);
            }
            // 类似：/rpc/top.yifan.service.DemoService/127.0.0.1:8080
            String serviceNodePath = URLUtil.fullURL(ZK_ROOT, rpcServiceName);
            // 注册服务监听器
            PathChildrenCache cache = registerNodeWatcher(serviceNodePath, rpcServiceName);
            List<ChildData> childDataList = cache.getCurrentData();
            if (CollectionUtils.isEmpty(childDataList)) {
                return Collections.emptyList();
            }
            List<Endpoint> serviceEndpoints = childDataList.stream().map(child -> {
                String address = child.getPath().replace(serviceNodePath + "/", "");
                String weight = child.getData() == null ? "1" : new String(child.getData(), StandardCharsets.UTF_8);

                String[] split = address.split(":");
                Endpoint endpoint = new Endpoint(split[0], Integer.parseInt(split[1]));
                endpoint.setWeight(Integer.parseInt(weight));

                return endpoint;
            }).collect(Collectors.toList());
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceEndpoints);
            return serviceEndpoints;
        }
    }

    private PathChildrenCache registerNodeWatcher(String serviceNodePath, String rpcServiceName) {
        try {
            return zookeeperTemplate.watchChildrenForNodePath(serviceNodePath, (client, event) -> {
                List<String> childNodes = client.getChildren().forPath(serviceNodePath);
                List<Endpoint> endpoints = new ArrayList<>();
                for (String child : childNodes) {
                    byte[] dataBytes = client.getData().forPath(child);
                    String weight = dataBytes == null ? "1" : new String(dataBytes, StandardCharsets.UTF_8);

                    String[] split = child.split(":");
                    Endpoint endpoint = new Endpoint(split[0], Integer.parseInt(split[1]));
                    endpoint.setWeight(Integer.parseInt(weight));
                    endpoints.add(endpoint);
                }
                SERVICE_ADDRESS_MAP.put(rpcServiceName, endpoints);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
