package top.yifan.rpc.remoting.transport.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import top.yifan.constants.*;
import top.yifan.rpc.exchange.DefaultExchangeFuture;
import top.yifan.rpc.exchange.Message;
import top.yifan.rpc.exchange.Response;

import java.net.InetSocketAddress;

/**
 * NettyClientHandler
 *
 * @author Star Zheng
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private final NettyClient client;

    public NettyClientHandler(NettyClient client) {
        this.client = client;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            log.info("Client receive msg: [{}]", msg);
            if (msg instanceof Message) {
                Message responseMsg = (Message) msg;
                byte messageType = responseMsg.getMType();
                if (messageType == MessageType.HEARTBEAT.getCode()) {
                    log.info("Heartbeat [{}]", responseMsg.getData());
                    return;
                }
                if (messageType == MessageType.RESPONSE.getCode()) {
                    System.out.println("client recve: " + responseMsg.getData());
                    DefaultExchangeFuture.sent((Response) responseMsg.getData());
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyChannel.removeChannel((InetSocketAddress) ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            log.info("Idle happen [{}]", ctx.channel().remoteAddress());
            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
            NettyChannel channel = NettyChannel.getOrNewChannel(address, () -> {
                try {
                    return client.doConnect(address);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            if (log.isDebugEnabled()) {
                log.debug("IdleStateEvent triggered, send heartbeat to channel " + channel);
            }
            // 构建消息体
            Message message = new Message();
            message.setMType(MessageType.HEARTBEAT.getCode());
            message.setCodec(SerializationType.PROTOSTUFF.getCode());
            message.setCompress(CompressType.GZIP.getCode());
            message.setData(CommonConstants.HEARTBEAT_EVENT);
            channel.send(message);

        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Client exception", cause);
        ctx.channel().close();
    }
}
