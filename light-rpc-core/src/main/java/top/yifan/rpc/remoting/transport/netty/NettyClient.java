package top.yifan.rpc.remoting.transport.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import top.yifan.constants.*;
import top.yifan.exception.RemotingException;
import top.yifan.rpc.exchange.DefaultExchangeFuture;
import top.yifan.rpc.exchange.Message;
import top.yifan.rpc.exchange.Request;
import top.yifan.rpc.exchange.Response;
import top.yifan.rpc.properties.RpcProperties;
import top.yifan.rpc.remoting.transport.AbstractClient;

import java.net.InetSocketAddress;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static top.yifan.constants.CommonConstants.*;

/**
 * @author Star Zheng
 */
@Slf4j
public class NettyClient extends AbstractClient {

    private Bootstrap bootstrap;

    private EventLoopGroup eventLoopGroup;

    public NettyClient() throws RemotingException {
        super();
    }

    public Channel doConnect(InetSocketAddress address) throws Exception {
        ChannelFuture channelFuture = bootstrap.connect(address);
        boolean connected = channelFuture.awaitUninterruptibly(getConnectionTimeout(), MILLISECONDS);

        if (connected && channelFuture.isSuccess()) {
            log.info("The client has connected [{}] success.", address.toString());
            return channelFuture.channel();
        } else if (channelFuture.cause() != null) {
            // Failed to connect to provider server by other reason
            Throwable cause = channelFuture.cause();
            log.error("Failed to connect to provider server by other reason.", cause);
            throw new RemotingException("Client failed to connect to server " + address + ", error message is:" + cause.getMessage(), cause);
        } else {
            // Client timeout
            RemotingException remotingException = new RemotingException("client failed to connect to server " + address + " client  timeout " + 60000);
            log.error("Client timeout.", remotingException);
            throw remotingException;
        }
    }

    @Override
    protected void doOpen() {
        bootstrap = new Bootstrap();
        this.initBootstrap(new NettyClientHandler(this));
    }

    @Override
    protected void doClose() {
        Future<?> future = eventLoopGroup.shutdownGracefully();
        // ?????? shutdownGracefully() ???????????????????????????????????????????????? group ????????????
        future.syncUninterruptibly();
    }

    @Override
    protected Response doSend(InetSocketAddress serviceAddress, Request request) throws Exception {
        NettyChannel nettyChannel = NettyChannel.getOrNewChannel(serviceAddress, () -> {
            try {
                return doConnect(serviceAddress);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        if (!nettyChannel.isActive()) {
            throw new RemotingException("Failed to send message, cause: Channel inactive, channel: -> " + serviceAddress);
        }

        // ???????????????
        Message message = new Message();
        message.setMsgType(MessageType.REQUEST.getCode());
        message.setCodec(getCodec().getCode());
        message.setCompress(getCompressor().getCode());
        message.setData(request);

        // ??????????????????Future
        DefaultExchangeFuture resultFuture = DefaultExchangeFuture.newFuture(message.getMsgId());
        // ????????????
        nettyChannel.send(message, 30000);

        return (Response) resultFuture.get(30000, MILLISECONDS);
    }

    private void initBootstrap(NettyClientHandler nettyClientHandler) {
        bootstrap = new Bootstrap();
        eventLoopGroup = createEventLoopGroup();
        bootstrap.group(eventLoopGroup).channel(NettyEventLoopFactory.socketChannelClass())
                // TCP???????????????Nagle????????????????????????????????????????????????????????????????????????????????????TCP_NODELAY ??????????????????????????????????????? Nagle ?????????
                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                // ????????????TCP??????????????????
                .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                // The timeout period of the connection.
                // If this time is exceeded or the connection cannot be established, the connection fails.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        // ???????????????
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                NettyCodecAdapter codec = new NettyCodecAdapter(CodecConstants.CODEC_TRANSPROT);
                channel.pipeline()
                        // for debug
                        .addLast("logging", new LoggingHandler(LogLevel.INFO)).addLast("decoder", codec.getDecoder()).addLast("encoder", codec.getEncoder())
                        // If no data is read to the server within 60 seconds, a heartbeat request is sent
                        .addLast("client-idle-handler", new IdleStateHandler(60 * 1000, 0, 0, MILLISECONDS)).addLast("handler", nettyClientHandler);
            }
        });
    }

    private EventLoopGroup createEventLoopGroup() {
        return NettyEventLoopFactory.eventLoopGroup(CommonConstants.DEFAULT_IO_THREADS, "NettyClientWorker");
    }

    private int getConnectionTimeout() {
        return RpcProperties.getParameter(CLIENT_CONNECTION_TIMEOUT_KEY, DEFAULT_CLIENT_CONNECTION_TIMEOUT);
    }

    private SerializationType getCodec() {
        String codec = RpcProperties.getParameter(REMOTE_CODEC_KEY, DEFAULT_REMOTE_CODEC);
        return SerializationType.getInstance(codec);
    }

    private CompressorType getCompressor() {
        String compress = RpcProperties.getParameter(REMOTE_COMPRESS_KEY, DEFAULT_REMOTE_COMPRESS);
        return CompressorType.getInstance(compress);
    }
}
