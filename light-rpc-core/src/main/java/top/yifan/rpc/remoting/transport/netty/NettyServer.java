package top.yifan.rpc.remoting.transport.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import top.yifan.constants.CodecConstants;
import top.yifan.constants.CommonConstants;
import top.yifan.rpc.properties.RpcProperties;
import top.yifan.rpc.remoting.transport.AbstractServer;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static top.yifan.constants.CommonConstants.RPC_PROTOCOL_PORT;

/**
 * @author Star Zheng
 */
@Slf4j
public class NettyServer extends AbstractServer {

    private ServerBootstrap serverBootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @Override
    protected void doOpen() throws Exception {
        serverBootstrap = new ServerBootstrap();
        bossGroup = createBossGroup();
        workerGroup = createWorkerGroup();
        initServerBootstrap();
        // 绑定端口，同步等待直到绑定成功
        ChannelFuture channelFuture = serverBootstrap.bind(getPort()).sync();
        log.info("Server is ready to receive request from client export at {}", getPort());
        // 等待服务端监听端口关闭
        channelFuture.channel().closeFuture().sync();
    }

    @Override
    protected void doClose() {
        Future<?> bossGroupFuture = bossGroup.shutdownGracefully();
        Future<?> workerGroupFuture = workerGroup.shutdownGracefully();
        // 阻塞直到关闭完成
        bossGroupFuture.syncUninterruptibly();
        workerGroupFuture.syncUninterruptibly();
    }

    private void initServerBootstrap() {
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NettyEventLoopFactory.serverSocketChannelClass())
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                // 表示系统用于临时存放已完成三次握手的请求的队列的最大长度，
                // 如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        NettyCodecAdapter adapter = new NettyCodecAdapter(CodecConstants.CODEC_TRANSPROT);
                        // idleTimeout should be at least more than twice heartBeat because possible retries of client.
                        int allIdleTime = CommonConstants.DEFAULT_HEARTBEAT * 3;
                        ch.pipeline()
                                .addLast("decoder", adapter.getDecoder())
                                .addLast("encoder", adapter.getEncoder())
                                .addLast("server-idle-handler", new IdleStateHandler(0, 0, allIdleTime, MILLISECONDS))
                                .addLast("handler", new NettyServerHandler());
                    }
                });
    }

    private EventLoopGroup createBossGroup() {
        return NettyEventLoopFactory.eventLoopGroup(1, "NettyServerBoss");
    }

    private EventLoopGroup createWorkerGroup() {
        return NettyEventLoopFactory.eventLoopGroup(CommonConstants.DEFAULT_IO_THREADS, "NettyServerWorker");
    }

    private int getPort() {
        String portStr = RpcProperties.getParameter(RPC_PROTOCOL_PORT);
        return StringUtils.isBlank(portStr) ? 8080 : Integer.parseInt(portStr);
    }
}
