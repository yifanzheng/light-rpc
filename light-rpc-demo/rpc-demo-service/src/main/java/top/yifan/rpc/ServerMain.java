package top.yifan.rpc;

import top.yifan.extension.ExtensionLoader;
import top.yifan.rpc.config.ServiceConfig;
import top.yifan.rpc.provider.ServiceProvider;
import top.yifan.rpc.remoting.transport.netty.NettyServer;

/**
 * @author Star Zheng
 */
public class ServerMain {

    public static void main(String[] args) {
        ServiceConfig serviceConfig = ServiceConfig.builder()
                .group("").version("").weight(2)
                .service(new DemoServiceImpl())
                .build();
        // 发布服务
        ServiceProvider provider = ExtensionLoader.getExtensionLoader(ServiceProvider.class).getExtension("zookeeper");
        provider.registerService(serviceConfig);

        NettyServer nettyServer = new NettyServer();
        nettyServer.start();
    }
}
