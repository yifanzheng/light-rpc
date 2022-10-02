package top.yifan.rpc.registry.zookeeper;

import top.yifan.rpc.config.ServiceConfig;
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

    /**
     * 如果使用临时节点：那么断开连接的时候，将zookeeper将自动消失。
     * 好处是如果服务端异常关闭，也不会有垃圾数据。<br>
     * 坏处是如果和zookeeper的网络闪断也通知客户端，客户端以为是服务端下线<br>
     * <p>
     * 如果使用永久节点：好处是网络闪断时不会影响服务端，而是由客户端进行自己判断长连接<br>
     * 坏处是服务端如果是异常关闭（无反注册），那么数据里就由垃圾节点，得由另外的哨兵程序进行判断
     *
     * @param serviceConfig 服务配置
     * @param socketAddress 服务地址
     */
    @Override
    public void register(ServiceConfig serviceConfig, InetSocketAddress socketAddress) {
        // 类似：/rpc/top.yifan.service.DemoService/127.0.0.1:8080
        String nodePath = URLUtil.fullURL(ZK_ROOT, serviceConfig.getRpcServiceName() + socketAddress.toString());
        this.zookeeperTemplate.createEphemeral(nodePath, String.valueOf(serviceConfig.getWeight()));
    }
}
