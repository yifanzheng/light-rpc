package top.yifan.rpc.remoting.transport;

import lombok.extern.slf4j.Slf4j;
import top.yifan.exception.RemotingException;
import top.yifan.extension.ExtensionLoader;
import top.yifan.rpc.domain.Endpoint;
import top.yifan.rpc.exchange.Request;
import top.yifan.rpc.exchange.Response;
import top.yifan.rpc.properties.RpcProperties;
import top.yifan.rpc.registry.ServiceDiscovery;

import java.net.InetSocketAddress;
import java.util.List;

import static top.yifan.constants.CommonConstants.SUBSCRIBE_PROTOCOL_KEY;

/**
 * @author Star Zheng
 */
@Slf4j
public abstract class AbstractClient implements RemotingClient {

    private final ServiceDiscovery serviceDiscovery;

    protected AbstractClient() throws RemotingException {
        try {
            doOpen();
        } catch (Exception e) {
            log.warn("Client start fail, message: {}", e.getMessage());
            close();
            throw new RemotingException("Failed to start connect.", e);
        }
        String protocol = RpcProperties.getParameter(SUBSCRIBE_PROTOCOL_KEY);
        serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension(protocol);
    }

    @Override
    public Response send(Request request) throws Exception {
        Endpoint endpoint = serviceDiscovery.lookup(request);
        return doSend(new InetSocketAddress(endpoint.getHost(), endpoint.getPort()), request);
    }

    @Override
    public void close() {
        doClose();
    }

    protected abstract void doOpen();

    protected abstract void doClose();

    protected abstract Response doSend(InetSocketAddress serviceAddress, Request request) throws Exception;
}
