package top.yifan.rpc.registry;

import java.util.List;

/**
 * ServiceDiscovery
 *
 * @author Star Zheng
 */
public interface ServiceDiscovery {

    List<String> lookup();
}
