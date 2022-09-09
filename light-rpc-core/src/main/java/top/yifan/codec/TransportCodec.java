package top.yifan.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import top.yifan.exchange.Message;
import top.yifan.extension.ExtensionLoader;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TransportCodec
 * <p>
 * netty传输编解码器
 *
 * @author Star Zheng
 */
public class TransportCodec implements Codec {

    private static final String NANE = "transport";

    @Override
    public byte[] encode(Object data, String serializeKey) {
        return null;
    }

    @Override
    public Object decode(byte[] data, String serializeKey) {
        return null;
    }


}
