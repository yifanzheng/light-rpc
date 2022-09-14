package top.yifan.rpc.registry.zookeeper;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

/**
 * ZKServiceDiscoveryLock
 *
 * @author Star Zheng
 */
public class ZKServiceDiscoveryLock {

    private static final Interner<String> LOCK_INTERNER = Interners.newWeakInterner();

    private ZKServiceDiscoveryLock() {
    }

    public static String buildLock(String lockValue) {
        return LOCK_INTERNER.intern(lockValue);
    }
}
