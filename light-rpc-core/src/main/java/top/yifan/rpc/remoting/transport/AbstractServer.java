package top.yifan.rpc.remoting.transport;

/**
 * AbstractServer
 *
 * @author Star Zheng
 */
public abstract class AbstractServer implements RemotingServer {

    @Override
    public void start() {
        try {
            doOpen();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close();
        }
    }

    @Override
    public void close() {
        doClose();
    }

    protected abstract void doOpen() throws Exception;

    protected abstract void doClose();


}
