package top.yifan.rpc.remoting.transport.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import top.yifan.constants.CommonConstants;
import top.yifan.constants.MessageType;
import top.yifan.exception.NotFoundServiceException;
import top.yifan.rpc.exchange.Message;
import top.yifan.rpc.exchange.Request;
import top.yifan.rpc.exchange.RequestData;
import top.yifan.rpc.exchange.Response;
import top.yifan.rpc.handler.RpcRequestHandler;
import top.yifan.util.StringUtils;

/**
 * @author Star Zheng
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final RpcRequestHandler rpcRequestHandler;

    public NettyServerHandler() {
        this.rpcRequestHandler = RpcRequestHandler.getInstance();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            log.info("Server received message: {}", msg);
            if (msg instanceof Message) {
                // 处理Client Request
                Message responseMsg = this.handleRequestMsg((Message) msg);
                // 响应请求
                ctx.writeAndFlush(responseMsg).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            // 确保ByteBuf内存释放，防止内存溢出
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // server will close channel when server don't receive any heartbeat from client util timeout.
        if (evt instanceof IdleStateEvent) {
            Channel channel = ctx.channel();
            log.info("IdleStateEvent triggered, close channel " + channel);
            channel.close();
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Client exception", cause);
        ctx.channel().close();
    }

    private Message handleRequestMsg(Message requestMsg) {
        Message responseMsg = new Message();
        responseMsg.setMsgId(requestMsg.getMsgId());
        responseMsg.setCodec(requestMsg.getCodec());
        responseMsg.setCompress(requestMsg.getCompress());

        if (requestMsg.getMsgType() == MessageType.REQUEST.getCode()) {
            Object data = handleRequest((Request) requestMsg.getData());
            responseMsg.setMsgType(MessageType.RESPONSE.getCode());
            responseMsg.setData(data);
        } else {
            responseMsg.setMsgType(MessageType.HEARTBEAT.getCode());
            responseMsg.setData(CommonConstants.HEARTBEAT_EVENT);
        }
        return responseMsg;
    }

    private Object handleRequest(Request request) {
        // 处理Client Request
        Response response = new Response();
        if (request.isBroken()) {
            Object data = request.getRequestData();

            String msg;
            if (data == null) {
                msg = null;
            } else if (data instanceof Throwable) {
                msg = StringUtils.toString((Throwable) data);
            } else {
                msg = data.toString();
            }
            response.setStatus(Response.BAD_REQUEST);
            response.setErrorMsg("Fail to decode request due to: " + msg);

            return response;
        }
        // 使用 handler 处理请求信息
        try {
            Object data = rpcRequestHandler.handler((RequestData) request.getRequestData());
            response.setStatus(Response.OK);
            response.setResult(data);
        } catch (NotFoundServiceException e) {
            response.setStatus(Response.SERVICE_NOT_FOUND);
            response.setErrorMsg(e.getMessage());
        } catch (Exception e) {
            response.setStatus(Response.SERVICE_ERROR);
            response.setErrorMsg(StringUtils.toString(e));
        }

        return response;

    }
}
