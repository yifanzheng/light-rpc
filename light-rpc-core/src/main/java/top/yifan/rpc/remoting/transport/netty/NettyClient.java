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
import top.yifan.rpc.remoting.transport.AbstractClient;

import java.net.InetSocketAddress;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

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
        // TODO connectTimeout 由配置信息传入
        boolean connected = channelFuture.awaitUninterruptibly(60000, MILLISECONDS);
        if (connected && channelFuture.isSuccess()) {
            log.info("The client has connected [{}] success.", address.toString());
            return channelFuture.channel();
        } else if (channelFuture.cause() != null) {
            // Failed to connect to provider server by other reason
            Throwable cause = channelFuture.cause();
            log.error("Failed to connect to provider server by other reason.", cause);
            throw new RemotingException("client failed to connect to server " + address + ", error message is:" + cause.getMessage(), cause);
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
        // 由于 shutdownGracefully() 是一个异步方法，这里需要阻塞直到 group 关闭完成
        future.syncUninterruptibly();
    }

    @Override
    protected Response doSend(Request request) throws Exception {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8080);
        NettyChannel nettyChannel = NettyChannel.getOrNewChannel(inetSocketAddress, () -> {
            try {
                return doConnect(inetSocketAddress);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        if (!nettyChannel.isActive()) {
            throw new RemotingException("Failed to send message, cause: Channel inactive, channel: -> " + inetSocketAddress);
        }
        // 结果回调Future
        DefaultExchangeFuture resultFuture = DefaultExchangeFuture.newFuture("hello");

        Message message = new Message();
        message.setMType(MessageType.REQUEST.getCode());
        message.setCodec(SerializationType.PROTOSTUFF.getCode());
        message.setCompress(CompressType.GZIP.getCode());
        message.setData(request);
        // 发送请求
        nettyChannel.send(message, 30000);

        return (Response) resultFuture.get(30000, MILLISECONDS);
    }

    private void initBootstrap(NettyClientHandler nettyClientHandler) {
        bootstrap = new Bootstrap();
        eventLoopGroup = createEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                .channel(NettyEventLoopFactory.socketChannelClass())
                // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据块，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                // 是否开启 TCP 底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                // The timeout period of the connection.
                // If this time is exceeded or the connection cannot be established, the connection fails.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        // 设置处理器
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                NettyCodecAdapter codec = new NettyCodecAdapter(CodecConstants.CODEC_TRANSPROT);
                channel.pipeline()
                        // for debug
                        .addLast("logging", new LoggingHandler(LogLevel.INFO))
                        .addLast("decoder", codec.getDecoder())
                        .addLast("encoder", codec.getEncoder())
                        // If no data is read to the server within 60 seconds, a heartbeat request is sent
                        .addLast("client-idle-handler", new IdleStateHandler(60 * 1000, 0, 0, MILLISECONDS))
                        .addLast("handler", nettyClientHandler);
            }
        });
    }

    private EventLoopGroup createEventLoopGroup() {
        return NettyEventLoopFactory.eventLoopGroup(CommonConstants.DEFAULT_IO_THREADS, "NettyClientWorker");
    }
}
