package top.yifan.remoting.transport.netty;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import top.yifan.constants.CommonConstants;

import java.util.concurrent.ThreadFactory;

/**
 * @author Star Zheng
 */
public class NettyEventLoopFactory {

    private NettyEventLoopFactory() {
    }

    public static EventLoopGroup eventLoopGroup(int threads, String threadFactoryName) {
        ThreadFactory threadFactory = new DefaultThreadFactory(threadFactoryName, true);
        return shouldEpoll() ? new EpollEventLoopGroup(threads, threadFactory) :
                new NioEventLoopGroup(threads, threadFactory);
    }

    public static Class<? extends SocketChannel> socketChannelClass() {
        return shouldEpoll() ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    private static boolean shouldEpoll() {
        if (Boolean.parseBoolean(System.getProperty(CommonConstants.NETTY_EPOLL_ENABLE_KEY, "false"))) {
            String osName = System.getProperty(CommonConstants.OS_NAME_KEY);
            return osName.toLowerCase().contains(CommonConstants.OS_LINUX_PREFIX) && Epoll.isAvailable();
        }
        return false;
    }
}
