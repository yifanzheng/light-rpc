package top.yifan.rpc.proxy;

import top.yifan.exception.RpcException;
import top.yifan.rpc.exchange.Request;
import top.yifan.rpc.exchange.RequestData;
import top.yifan.rpc.exchange.Response;
import top.yifan.rpc.remoting.transport.RemotingClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * PRC 客户端代理
 *
 * @author Star Zheng
 */
public class RpcClientProxy implements Proxy {

    private final RemotingClient client;

    public RpcClientProxy(RemotingClient client) {
        this.client = client;
    }

    public Object getProxy(Class<?> clazz) {
        return java.lang.reflect.Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new DefaultInvocationHandler());
    }

    private class DefaultInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 构建请求体
            RequestData requestData = new RequestData();
            requestData.setInterfaceName(method.getDeclaringClass().getName());
            requestData.setMethodName(method.getName());
            requestData.setParamTypes(method.getParameterTypes());
            requestData.setParameters(args);

            Request request = new Request();
            request.setRequestData(requestData);
            // 发送请求
            Response response = client.send(request);

            checkValid(response);
            return response.getResult();
        }

        private void checkValid(Response response) {
            if (response == null) {
                throw new RpcException("Service invocation fail.");
            }
            if (response.getStatus() <= 0 || response.getStatus() != Response.OK) {
                throw new RpcException(response.getErrorMsg());
            }
        }
    }


}
