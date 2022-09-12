package top.yifan.rpc.registry.zookeeper;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import top.yifan.exception.NoSuchNodeException;
import top.yifan.exception.ZookeeperException;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * ZookeeperTemplate
 *
 * @author sz7v
 */
public class ZookeeperTemplate {

    private final ZookeeperClient zookeeperClient;

    public ZookeeperTemplate(ZookeeperClient zookeeperClient) {
        this.zookeeperClient = zookeeperClient;
    }

    /**
     * 创建节点
     *
     * @param nodePath   节点路径
     * @param data       数据
     * @param createMode 节点类型
     * @return 节点全路径
     */
    public String createNode(String nodePath, String data, CreateMode createMode) {
        try {
            CuratorFramework client = this.zookeeperClient.getClient();
            return client.create()
                    .creatingParentsIfNeeded()
                    .withMode(createMode)
                    .forPath(nodePath, data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new ZookeeperException("Create Zookeeper node[" + nodePath + "] error", e);
        }
    }

    /**
     * 获取节点数据
     *
     * @param nodePath 节点路径
     * @return data string
     */
    public String getData(String nodePath) {
        try {
            CuratorFramework client = this.zookeeperClient.getClient();
            byte[] dataBytes = client.getData().forPath(nodePath);
            return new String(dataBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            if (e instanceof KeeperException.NoNodeException) {
                return null;
            }
            throw new ZookeeperException("Get Zookeeper node[" + nodePath + "] error", e);
        }
    }

    /**
     * 设定指定节点的数据
     *
     * @param nodePath 路径
     * @param data     数据
     * @return 节点Stat
     */
    public Stat setData(String nodePath, String data) {
        Preconditions.checkArgument(StringUtils.isBlank(nodePath), "NodePath cannot be empty");
        try {
            CuratorFramework client = this.zookeeperClient.getClient();
            return client.setData().forPath(nodePath, data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            if (e instanceof KeeperException.NoNodeException) {
                throw new NoSuchNodeException("No such config for node path: " + nodePath, e);
            }
            throw new ZookeeperException("Cannot set data for path: " + nodePath, e);
        }
    }

    public void deleteNode(String nodePath) {
        try {
            CuratorFramework client = this.zookeeperClient.getClient();
            client.delete().deletingChildrenIfNeeded().forPath(nodePath);
        } catch (Exception e) {
            if (e instanceof KeeperException.NoNodeException) {
                return;
            }
            throw new ZookeeperException("Delete Zookeeper node[" + nodePath + "] error", e);
        }
    }

    /**
     * 检查一个路径是否存在
     *
     * @param nodePath 节点路径
     * @return 存在则返回true，否则返回false
     */
    public boolean isExists(String nodePath) {
        Preconditions.checkArgument(StringUtils.isBlank(nodePath), "NodePath cannot be empty");
        try {
            CuratorFramework client = this.zookeeperClient.getClient();
            Stat stat = client.checkExists().forPath(nodePath);
            return Objects.nonNull(stat);
        } catch (Exception e) {
            throw new ZookeeperException("Cannot check exists for path: " + nodePath, e);
        }
    }

}
