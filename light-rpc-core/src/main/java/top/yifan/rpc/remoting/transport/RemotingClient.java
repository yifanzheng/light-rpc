package top.yifan.rpc.remoting.transport;

import top.yifan.rpc.exchange.Request;
import top.yifan.rpc.exchange.Response;

/**
 * @author Star Zheng
 */
public interface RemotingClient {
    Response send(Request request) throws Exception;

    void close();

}
