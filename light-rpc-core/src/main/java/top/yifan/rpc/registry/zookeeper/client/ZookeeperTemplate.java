package top.yifan.rpc.registry.zookeeper.client;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yifan.exception.ZookeeperException;

import java.io.Closeable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * ZookeeperTemplate
 *
 * @author sz7v
 */
public class ZookeeperTemplate {

    private static final Logger log = LoggerFactory.getLogger(ZookeeperTemplate.class);

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final CuratorFramework client;

    public ZookeeperTemplate(ZookeeperClient zookeeperClient) {
        client = zookeeperClient.getClient();
    }

    public void createPersistent(String nodePath) {
        try {
            client.create().creatingParentsIfNeeded().forPath(nodePath);
        } catch (KeeperException.NodeExistsException e) {
            log.warn("ZNode [{}] already exists.", nodePath, e);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void createEphemeral(String nodePath) {
        try {
            client.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL).forPath(nodePath);
        } catch (KeeperException.NodeExistsException e) {
            log.warn("ZNode " + nodePath + " already exists, since we will only try to recreate a node on a session expiration" +
                    ", this duplication might be caused by a delete delay from the zk server, which means the old expired session" +
                    " may still holds this ZNode and the server just hasn't got time to do the deletion. In this case, " +
                    "we can just try to delete and create again.", e);
            deleteNode(nodePath);
            createEphemeral(nodePath);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void createPersistent(String nodePath, String data) {
        byte[] dataBytes = data.getBytes(CHARSET);
        try {
            client.create()
                    .creatingParentsIfNeeded()
                    .forPath(nodePath, dataBytes);
        } catch (KeeperException.NodeExistsException e) {
            try {
                client.setData().forPath(nodePath, dataBytes);
            } catch (Exception e1) {
                throw new IllegalStateException(e.getMessage(), e1);
            }
            log.warn("ZNode [{}] already exists.", nodePath, e);
        } catch (Exception e) {
            throw new ZookeeperException("Create Zookeeper node[" + nodePath + "] error", e);
        }
    }

    public void createEphemeral(String nodePath, String data) {
        byte[] dataBytes = data.getBytes(CHARSET);
        try {
            client.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL).forPath(nodePath, dataBytes);
        } catch (KeeperException.NodeExistsException e) {
            log.warn("ZNode " + nodePath + " already exists, since we will only try to recreate a node on a session expiration" +
                    ", this duplication might be caused by a delete delay from the zk server, which means the old expired session" +
                    " may still holds this ZNode and the server just hasn't got time to do the deletion. In this case, " +
                    "we can just try to delete and create again.", e);
            deleteNode(nodePath);
            createEphemeral(nodePath);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public PathChildrenCache watchChildrenForNodePath(String nodePath, PathChildrenCacheListener listener) throws Exception {
        // 监听所有子节点变化
        PathChildrenCache cache = new PathChildrenCache(client, nodePath, true);
        cache.getListenable().addListener(listener);
        cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        return cache;
    }

    public List<String> getChildren(String path) {
        try {
            return client.getChildren().forPath(path);
        } catch (KeeperException.NoNodeException e) {
            return Collections.emptyList();
        } catch (Exception e) {
            throw new ZookeeperException(e.getMessage(), e);
        }
    }

    public void deleteNode(String nodePath) {
        try {
            client.delete().deletingChildrenIfNeeded().forPath(nodePath);
        } catch (KeeperException.NoNodeException ignore) {
        } catch (Exception e) {
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
            if (client.checkExists().forPath(nodePath) != null) {
                return true;
            }
        } catch (Exception e) {
            log.error("Cannot check exists for path: {}", nodePath, e);
        }
        return false;
    }

}
