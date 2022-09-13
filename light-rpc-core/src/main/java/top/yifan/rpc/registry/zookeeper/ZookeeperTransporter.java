package top.yifan.rpc.registry.zookeeper;

import top.yifan.rpc.registry.zookeeper.client.ZookeeperClient;
import top.yifan.rpc.registry.zookeeper.client.ZookeeperTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ZookeeperTransporter
 *
 * @author Star Zheng
 */
public class ZookeeperTransporter {

    private static final Map<String, ZookeeperTemplate> ZK_MAP = new ConcurrentHashMap<>();

    private ZookeeperTransporter() {
    }

    public ZookeeperTemplate connect(String connectUrl) {
        if (ZK_MAP.containsKey(connectUrl)) {
            return ZK_MAP.get(connectUrl);
        }
        return ZK_MAP.computeIfAbsent(connectUrl, k -> new ZookeeperTemplate(new ZookeeperClient(connectUrl)));
    }

    public static ZookeeperTransporter getInstance() {
        return ZookeeperTransporterHolder.INSTANCE;
    }

    private static class ZookeeperTransporterHolder {
        private static final ZookeeperTransporter INSTANCE = new ZookeeperTransporter();
    }
}
