package top.yifan.remoting.transport.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import top.yifan.constants.CodecConstants;
import top.yifan.constants.CommonConstants;
import top.yifan.constants.CompressType;
import top.yifan.constants.SerializationType;
import top.yifan.exchange.DefaultFuture;
import top.yifan.exchange.Message;
import top.yifan.exchange.Response;

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
            log.info("client receive msg: [{}]", msg);
            if (msg instanceof Message) {
                Message tmp = (Message) msg;
                byte messageType = tmp.getMType();
                if (messageType == CodecConstants.HEARTBEAT_TYPE) {
                    log.info("heart [{}]", tmp.getData());
                } else if (messageType == CodecConstants.RESPONSE_TYPE) {
                    System.out.println(tmp.getData());
                    //Response response = (Response) tmp.getData();
                    //DefaultFuture.sent(response.getRequestId(), response);
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
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("write idle happen [{}]", ctx.channel().remoteAddress());
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
                Message message = new Message();
                message.setCodec(SerializationType.PROTOSTUFF.getCode());
                message.setCompress(CompressType.GZIP.getCode());
                message.setMType(CodecConstants.HEARTBEAT_TYPE);
                message.setData(CommonConstants.HEARTBEAT_EVENT);
                channel.send(message);
            }
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
