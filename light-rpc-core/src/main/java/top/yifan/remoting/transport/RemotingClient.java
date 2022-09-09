package top.yifan.remoting.transport;

import top.yifan.exchange.Request;

/**
 * @author Star Zheng
 */
public interface RemotingClient {
    void send(Request request);

    void close();

}
