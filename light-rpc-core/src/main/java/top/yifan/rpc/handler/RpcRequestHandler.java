package top.yifan.rpc.handler;

import top.yifan.exception.NotFoundServiceException;
import top.yifan.extension.ExtensionLoader;
import top.yifan.rpc.exchange.Request;
import top.yifan.rpc.exchange.RequestData;
import top.yifan.rpc.exchange.Response;
import top.yifan.rpc.properties.RpcProperties;
import top.yifan.rpc.provider.ServiceProvider;

import java.lang.reflect.Method;

import static top.yifan.constants.CommonConstants.DEFAULT_REGISTRY_PROTOCOL;
import static top.yifan.constants.CommonConstants.REGISTRY_PROTOCOL_KEY;

/**
 * RpcRequestHandler
 *
 * @author Star Zheng
 */
public class RpcRequestHandler {

    private final ServiceProvider serviceProvider;

    private RpcRequestHandler() {
        String protocol = RpcProperties.getParameter(REGISTRY_PROTOCOL_KEY, DEFAULT_REGISTRY_PROTOCOL);
        serviceProvider = ExtensionLoader.getExtensionLoader(ServiceProvider.class).getExtension(protocol);
    }

    public Object handler(RequestData request) throws Exception {
        // 获取指定服务，并执行指定方法
        Object obj = serviceProvider.getService(request.getRpcServiceName());
        if (obj == null) {
            throw new NotFoundServiceException("Not found service");
        }
        Method method = obj.getClass().getMethod(request.getMethodName(), request.getParamTypes());
        method.setAccessible(true);

        return method.invoke(obj, request.getParameters());
    }

    public static RpcRequestHandler getInstance() {
        return RpcRequestHandlerHolder.INSTANCE;
    }

    private static class RpcRequestHandlerHolder {
        private static final RpcRequestHandler INSTANCE = new RpcRequestHandler();
    }
}
