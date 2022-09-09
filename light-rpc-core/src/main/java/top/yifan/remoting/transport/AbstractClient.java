package top.yifan.remoting.transport;

import top.yifan.exception.RemotingException;
import top.yifan.exchange.Request;

/**
 * @author Star Zheng
 */
public abstract class AbstractClient implements RemotingClient {

    public AbstractClient() throws RemotingException {
        try {
            doOpen();
        } catch (Exception e) {
            throw new RemotingException("Failed to start connect.", e);
        } finally {
            close();
        }
    }

    @Override
    public void send(Request request) {

    }

    @Override
    public void close() {
        doClose();
    }

    protected abstract void doOpen();

    protected abstract void doClose();
}
