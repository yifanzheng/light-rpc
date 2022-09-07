package top.yifan.remoting.transport.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import top.yifan.config.Config;
import top.yifan.constants.CommonConstants;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Star Zheng
 */
public class NettyClient {

    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;

    public NettyClient(Config config) {

    }

    public void doOpen() {

    }

    public void doConnect() {

    }

    public void doClose() {

    }

    private void initBootstrap() {
        eventLoopGroup = NettyEventLoopFactory.eventLoopGroup(CommonConstants.DEFAULT_IO_THREADS, "NettyClientWorker");
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NettyEventLoopFactory.socketChannelClass())
                // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                .option(ChannelOption.TCP_NODELAY, true)
                // 是否开启 TCP 底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                // The timeout period of the connection.
                // If this time is exceeded or the connection cannot be established, the connection fails.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        // 设置处理器
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {

                channel.pipeline()
                        .addLast("logging", new LoggingHandler(LogLevel.INFO))//for debug
                        // TODO 根据配置获取解码器和编码器
                        .addLast("decoder", null)
                        .addLast("encoder", null)
                        // If no data is sent to the server within 15 seconds, a heartbeat request is sent
                        .addLast("client-idle-handler", new IdleStateHandler(60 * 1000, 0, 0, MILLISECONDS))
                        .addLast("handler", new NettyClientHandler());
            }
        });
    }
}
