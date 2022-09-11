package top.yifan.rpc.handler;

import top.yifan.rpc.example.DemoServiceImpl;
import top.yifan.rpc.exchange.Request;
import top.yifan.rpc.exchange.Response;

import java.lang.reflect.Method;

/**
 * TODO 完善 RpcRequestHandler
 *
 * @author Star Zheng
 */
public class RpcRequestHandler {

    private RpcRequestHandler() {
    }

    public Object handler(Request request) {
        try {
            // TODO 配置中心获取
            // 获取指定服务，并执行指定方法
            Object obj = Class.forName(DemoServiceImpl.class.getName()).newInstance();
            Method method = obj.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            method.setAccessible(true);
            Object result = method.invoke(obj, request.getParameters());

            Response response = new Response();
            response.setRequestId(request.getRequestId());
            response.setStatus(Response.OK);
            response.setResult(result);

            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static RpcRequestHandler getInstance() {
        return RpcRequestHandlerHolder.INSTANCE;
    }

    private static class RpcRequestHandlerHolder {
        private static final RpcRequestHandler INSTANCE = new RpcRequestHandler();
    }
}
