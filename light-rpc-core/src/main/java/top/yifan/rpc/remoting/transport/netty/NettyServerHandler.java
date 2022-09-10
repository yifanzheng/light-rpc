package top.yifan.rpc.remoting.transport.netty;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import top.yifan.constants.CommonConstants;
import top.yifan.constants.MessageType;
import top.yifan.rpc.exchange.Message;
import top.yifan.rpc.exchange.Request;
import top.yifan.rpc.handler.RpcRequestHandler;

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
        MessageType mType;
        Object data;
        if (requestMsg.getMType() == MessageType.REQUEST.getCode()) {
            mType = MessageType.RESPONSE;
            // 处理Client Request
            data = rpcRequestHandler.handler(JSON.parseObject(JSON.toJSONString(requestMsg.getData()), Request.class));
        } else { // Heartbeat
            mType = MessageType.HEARTBEAT;
            data = CommonConstants.HEARTBEAT_EVENT;
        }
        Message responseMsg = new Message();
        responseMsg.setMId(requestMsg.getMId());
        responseMsg.setMType(mType.getCode());
        responseMsg.setCodec(requestMsg.getCodec());
        responseMsg.setCompress(requestMsg.getCompress());
        responseMsg.setData(data);

        return responseMsg;
    }
}
