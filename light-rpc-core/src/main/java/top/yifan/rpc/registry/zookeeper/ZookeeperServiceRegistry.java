package top.yifan.rpc.registry.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import top.yifan.rpc.registry.ServiceRegistry;
import top.yifan.rpc.registry.zookeeper.client.ZookeeperTemplate;
import top.yifan.util.URLUtil;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Properties;

import static top.yifan.constants.CommonConstants.ZK_ROOT;

/**
 * ZookeeperServiceRegistry
 *
 * @author Star Zheng
 */
public class ZookeeperServiceRegistry implements ServiceRegistry {

    private final ZookeeperTemplate zookeeperTemplate;

    public ZookeeperServiceRegistry() {
        // TODO 完善
        //InputStream stream = this.getClass().getResourceAsStream("");
        //Properties properties =new Properties();
        //properties.load(stream);
        //String zkAddress = properties.getProperty("rpc.zookeeper.address");
        this.zookeeperTemplate = ZookeeperTransporter.getInstance().connect("");
    }

    @Override
    public void register(String rpcServiceName, InetSocketAddress socketAddress) {
        // 类似：/rpc/top.yifan.service.DemoService/127.0.0.1:8080
        String nodePath = URLUtil.fullURL(ZK_ROOT, rpcServiceName + socketAddress.toString());
        this.zookeeperTemplate.createEphemeral(nodePath);
    }
}
