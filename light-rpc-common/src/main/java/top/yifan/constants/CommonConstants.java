package top.yifan.constants;

/**
 * CommonConstants
 *
 * @author Star Zheng
 */
public class CommonConstants {

    private CommonConstants() {

    }

    public static final String OS_NAME_KEY = "os.name";
    public static final String OS_LINUX_PREFIX = "linux";
    public static final String NETTY_EPOLL_ENABLE_KEY = "netty.epoll.enable";
    public static final int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 16);

    public static final int DEFAULT_HEARTBEAT = 60 * 1000;

    public static final String HEARTBEAT_EVENT = null;

    public static final String ZK_ROOT = "/rpc";

    public static final String REGISTRY_PROTOCOL_KEY = "rpc.registry.protocol";
    public static final String DEFAULT_REGISTRY_PROTOCOL = "zookeeper";

    public static final String REGISTRY_ADDRESS_KEY = "rpc.registry.address";

    public static final String SUBSCRIBE_PROTOCOL_KEY = "rpc.subscribe.protocol";

    public static final String SUBSCRIBE_ADDRESS_KEY = "rpc.subscribe.address";
    public static final String CLIENT_CONNECTION_TIMEOUT_KEY = "rpc.client.connection.timeout";
    public static final int DEFAULT_CLIENT_CONNECTION_TIMEOUT = 60000;

    public static final String REMOTE_CODEC_KEY = "rpc.remote.codec";
    public static final String DEFAULT_REMOTE_CODEC = "hessian2";

    public static final String REMOTE_COMPRESS_KEY = "rpc.remote.compress";
    public static final String DEFAULT_REMOTE_COMPRESS = "identity";

    public static final String LOADBALANCE_STRATEGY_KEY = "rpc.loadbalance.strategy";

    public static final String RPC_PROTOCOL_PORT = "rpc.protocol.port";


}
