package top.yifan.rpc.registry.zookeeper;

import top.yifan.rpc.properties.RpcProperties;
import top.yifan.rpc.registry.ServiceRegistry;
import top.yifan.rpc.registry.zookeeper.client.ZookeeperTemplate;
import top.yifan.util.URLUtil;

import java.net.InetSocketAddress;

import static top.yifan.constants.CommonConstants.REGISTRY_ADDRESS_KEY;
import static top.yifan.constants.CommonConstants.ZK_ROOT;

/**
 * ZookeeperServiceRegistry
 *
 * @author Star Zheng
 */
public class ZookeeperServiceRegistry implements ServiceRegistry {

    private final ZookeeperTemplate zookeeperTemplate;

    public ZookeeperServiceRegistry() {
        String zkAddress = RpcProperties.getParameter(REGISTRY_ADDRESS_KEY);
        this.zookeeperTemplate = ZookeeperTransporter.getInstance().connect(zkAddress);
    }

    @Override
    public void register(String rpcServiceName, InetSocketAddress socketAddress) {
        // 类似：/rpc/top.yifan.service.DemoService/127.0.0.1:8080
        String nodePath = URLUtil.fullURL(ZK_ROOT, rpcServiceName + socketAddress.toString());
        this.zookeeperTemplate.createEphemeral(nodePath);
    }
}
