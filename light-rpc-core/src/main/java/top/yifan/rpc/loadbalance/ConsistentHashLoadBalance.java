package top.yifan.rpc.loadbalance;

/**
 * 一致性哈希负载均衡
 *
 * @author sz7v
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "consistenthash";

    @Override
    protected Endpoint doSelect() {
        return null;
    }
}
