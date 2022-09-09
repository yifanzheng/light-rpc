package top.yifan.remoting.transport.netty;

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import top.yifan.exception.RemotingException;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * NettyChannel
 *
 * @author Star Zheng
 */
public final class NettyChannel {

    private static final ConcurrentMap<InetSocketAddress, NettyChannel> CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * netty channel
     */
    private final Channel channel;

    private NettyChannel(InetSocketAddress address, Supplier<Channel> channelSupplier) {
        Preconditions.checkArgument(address != null, "socket address == null");
        this.channel = channelSupplier.get();
    }

    public void send(Object message, long timeout) throws RemotingException {
        if (this.isActive()) {
            throw new RemotingException("Failed to send message, cause: Channel inactive, channel: -> " + this.channel.remoteAddress());
        }
        boolean success;
        try {
            ChannelFuture channelFuture = this.channel.writeAndFlush(message);
            success = channelFuture.await(timeout);
            Throwable cause = channelFuture.cause();
            if (cause != null) {
                throw cause;
            }
        } catch (Throwable e) {
            removeChannel((InetSocketAddress) channel.remoteAddress());
            throw new RemotingException("Failed to send message to " + channel.remoteAddress().toString() + ", cause: " + e.getMessage(), e);
        }
        if (!success) {
            throw new RemotingException("Failed to send message to " + channel.remoteAddress().toString() + " in timeout(" + timeout + "ms) limit");
        }
    }

    public void send(Object message) {
        this.channel.writeAndFlush(message)
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    public boolean isActive() {
        return this.channel.isActive();
    }

    public void close() {
        this.channel.close();
    }

    static NettyChannel getOrNewChannel(InetSocketAddress address, Supplier<Channel> channelSupplier) {
        if (address == null) {
            return null;
        }
        NettyChannel result = CHANNEL_MAP.get(address);
        if (result == null) {
            NettyChannel nettyChannel = new NettyChannel(address, channelSupplier);
            result = CHANNEL_MAP.putIfAbsent(address, nettyChannel);
            if (result == null) {
                result = nettyChannel;
            }
        }

        return result;
    }

    static void removeChannel(InetSocketAddress address) {
        if (address != null) {
            NettyChannel nettyChannel = CHANNEL_MAP.remove(address);
            if (nettyChannel != null) {
                nettyChannel.close();
            }
        }
    }

}
