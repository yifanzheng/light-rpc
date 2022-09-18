package top.yifan.rpc;

import top.yifan.exception.RemotingException;
import top.yifan.rpc.proxy.RpcClientProxy;
import top.yifan.rpc.remoting.transport.netty.NettyClient;

/**
 * Main
 *
 * @author Star Zheng
 */
public class ClientMain {

    public static void main(String[] args) throws RemotingException {
        NettyClient nettyClient = new NettyClient();

        RpcClientProxy rpcClientProxy = new RpcClientProxy(nettyClient);
        DemoService proxy = (DemoService) rpcClientProxy.getProxy(DemoService.class);

        System.out.println(proxy.sayHello("rpc"));

        nettyClient.close();
    }
}
