package top.yifan.rpc.registry.zookeeper;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yifan.exception.ZookeeperException;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * ZookeeperClient
 *
 * @author sz7v
 */
public class ZookeeperClient implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(ZookeeperClient.class);

    private final String connectString;

    private CuratorFramework client;

    private volatile boolean isActived = false;

    public ZookeeperClient(String connectString) {
        this.connectString = connectString;
    }

    public CuratorFramework getClient() {
        connectIfAbsent();
        return client;
    }

    public CuratorFramework createConnect() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        // 以下 Zookeeper 连接配置在实际开发中，可从配置文件中获取
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(30000)
                .connectionTimeoutMs(10000)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        try {
            // 阻塞直到连接成功
            zkClient.blockUntilConnected(10000, TimeUnit.MILLISECONDS);
            if (!zkClient.getState().equals(CuratorFrameworkState.STARTED)) {
                throw new IllegalStateException("Zookeeper client initialization failed");
            }
            if (!zkClient.getZookeeperClient().isConnected()) {
                throw new IllegalStateException("Failed to connect to zookeeper server");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Connect to zookeeper failed, message: {}", e.getMessage());
            throw new ZookeeperException(e);
        }
        return zkClient;
    }

    private void connectIfAbsent() {
        if (client != null && isActived) {
            return;
        }
        synchronized (this) {
            if (client != null && isActived) {
                return;
            }
            if (StringUtils.isBlank(connectString)) {
                throw new IllegalArgumentException("Zookeeper connect string can't be empty");
            }
            // 以下 Zookeeper 连接配置在实际开发中，可从配置文件中获取
            client = this.createConnect();
            isActived = true;
            log.info("Connection Zookeeper at: {}", connectString);
        }
    }

    @Override
    public void close() throws IOException {
        if (client != null) {
            client.close();
            isActived = false;
        }
    }
}
