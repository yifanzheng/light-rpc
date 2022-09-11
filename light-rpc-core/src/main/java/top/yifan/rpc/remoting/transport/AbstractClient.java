package top.yifan.rpc.remoting.transport;

import lombok.extern.slf4j.Slf4j;
import top.yifan.exception.RemotingException;
import top.yifan.rpc.exchange.Request;
import top.yifan.rpc.exchange.Response;

/**
 * @author Star Zheng
 */
@Slf4j
public abstract class AbstractClient implements RemotingClient {

    protected AbstractClient() throws RemotingException {
        try {
            doOpen();
        } catch (Exception e) {
            log.warn("Client start fail, message: {}", e.getMessage());
            close();
            throw new RemotingException("Failed to start connect.", e);
        }
    }

    @Override
    public Response send(Request request) throws Exception {
        // TODO loadbalance
        return doSend(request);
    }

    @Override
    public void close() {
        doClose();
    }

    protected abstract void doOpen();

    protected abstract void doClose();

    protected abstract Response doSend(Request request) throws Exception;
}
