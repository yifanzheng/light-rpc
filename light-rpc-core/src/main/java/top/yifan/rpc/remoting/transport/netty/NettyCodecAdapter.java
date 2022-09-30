package top.yifan.rpc.remoting.transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import top.yifan.constants.CodecConstants;
import top.yifan.constants.CommonConstants;
import top.yifan.constants.MessageType;
import top.yifan.extension.ExtensionLoader;
import top.yifan.io.Bytes;
import top.yifan.rpc.codec.Codec;
import top.yifan.rpc.compressor.CompressorSupport;
import top.yifan.rpc.exchange.Message;
import top.yifan.rpc.exchange.Request;
import top.yifan.rpc.exchange.Response;

import java.util.Arrays;

import static top.yifan.constants.CodecConstants.*;

/**
 * Netty Codec 适配器，使用定长头+字节数组作为自定义编码协议（防止TCP沾包问题）
 * <pre>
 *      0     1     2     3      4        5        6        7         8   9   10   11    12    13    14   15
 *   +-----+-----+-----+-----+-------+--------+-------+-------------+---+---+----+----+-----+-----+-----+----+
 *   |   magic   number      |version|compress| codec | messageType |   messageId     |     dataLength       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         data                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4Byte magic number（魔法数）  1Byte version（版本）     1Byte compress（压缩类型）  1Byte codec（序列化类型）
 * 1Byte messageType（消息类型） 4Byte messageId（消息Id） 4Byte dataLength（消息长度）data（object类型数据）
 * </pre>
 * <p>
 * {@link LengthFieldBasedFrameDecoder} 是一个基于长度域的解码器，用于解决TCP沾包问题（不用我们手动处理）。
 * </p>
 *
 * @author Star Zheng
 * @see <a href="https://wenjie.store/archives/about-decoder-4">LengthFieldBasedFrameDecoder解码器</a>
 */
@Slf4j
public final class NettyCodecAdapter {

    private final ChannelHandler encoder = new InternalEncoder();

    private final ChannelHandler decoder = new InternalDecoder();

    private final Codec codec;

    public NettyCodecAdapter(String codecName) {
        this.codec = getCodec(codecName);
    }

    public ChannelHandler getEncoder() {
        return encoder;
    }

    public ChannelHandler getDecoder() {
        return decoder;
    }

    private Codec getCodec(String codecName) {
        ExtensionLoader<Codec> loader = ExtensionLoader.getExtensionLoader(Codec.class);
        if (loader.hasExtension(codecName)) {
            return loader.getExtension(codecName);
        }
        return loader.getExtension("default");
    }


    private class InternalEncoder extends MessageToByteEncoder<Message> {
        @Override
        protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf out) {
            try {
                // 1. header
                byte[] header = Bytes.copyOf(MAGIC_NUM, HEAD_LENGTH);
                // 设置 version, compress, codec, message type
                header[4] = VERSION;
                header[5] = message.getCompress();
                header[6] = message.getCodec();
                header[7] = message.getMsgType();
                // 设置 message id
                Bytes.int2bytes(message.getMsgId(), header, 8);

                // 2. encode request data
                byte[] dataBytes = null;
                // 如果是心跳数据，则不进行处理
                if (message.getMsgType() != MessageType.HEARTBEAT.getCode()) {
                    // 序列化对象
                    dataBytes = codec.encode(message.getData(), message.getCodec());
                    // 压缩数据
                    dataBytes = CompressorSupport.getCompressor(message.getCompress()).compress(dataBytes);
                }
                // 数据长度
                int dataLen = dataBytes != null ? dataBytes.length : 0;
                Bytes.int2bytes(dataLen, header, 12);
                // 3. 将header与request data写入ByteBuf
                out.writeBytes(header);
                if (dataBytes != null) {
                    out.writeBytes(dataBytes);
                }
            } catch (Exception e) {
                log.error("Encode request error!", e);
            }
        }

    }

    private class InternalDecoder extends LengthFieldBasedFrameDecoder {
        public InternalDecoder() {
            // lengthFieldOffset: magic code is 4 bytes, and version, compress, codec, message type is 1 byte, and message id is 4 bytes, and then data length. so value is 12
            // lengthFieldLength: data length is 4 byte. so value is 4.
            // lengthAdjustment: data length is actual length, so values is 0.
            // initialBytesToStrip: we need get all data, so value is 0.
            this(CodecConstants.MAX_FRAME_LENGTH, 12, 4, 0, 0);
        }

        /**
         * @param maxFrameLength      最大数据帧长度。如果接收的数据超过此长度，数据将被丢弃，并抛出 TooLongFrameException。
         * @param lengthFieldOffset   长度域偏移量。
         * @param lengthFieldLength   长度域字节数，即用几个字节来表示数据长度（此属性的值是数据长度）。
         * @param lengthAdjustment    数据长度修正（数据长度 + lengthAdjustment = 数据总长度）。长度域指定的长度可以是header+body的整个长度，也可以只是body的长度。如果表示header+body的整个长度，那么需要修正数据长度。
         * @param initialBytesToStrip 跳过的字节数。如果你需要接收header+body的所有数据，此值就是0；如果你只想接收body数据，那么需要跳过header所占用的字节数
         */
        public InternalDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
            super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
        }

        @Override
        protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
            Object decoded = super.decode(ctx, in);
            if (decoded instanceof ByteBuf) {
                ByteBuf data = (ByteBuf) decoded;
                if (data.readableBytes() < HEAD_LENGTH) {
                    return decoded;
                }
                try {
                    return decodeBody(data);
                } finally {
                    data.release();
                }
            }
            return decoded;
        }

        private Message decodeBody(ByteBuf in) {
            // 读取header
            byte[] header = new byte[HEAD_LENGTH];
            in.readBytes(header);
            // check magic num
            byte[] magicNum = Bytes.copyOf(header, 4);
            checkMagicNum(magicNum);
            // check version
            if (header[4] != VERSION) {
                throw new IllegalStateException("Versions are not consistent, expect version: [" + VERSION + "], actual version: [" + header[4] + "]");
            }

            byte compressType = header[5];
            byte codecType = header[6];
            byte messageType = header[7];
            int messageId = Bytes.bytes2int(header, 8);
            int dataLength = Bytes.bytes2int(header, 12);
            // 构建消息体
            Message message = new Message(messageId, messageType, codecType, compressType);
            if (messageType == MessageType.HEARTBEAT.getCode()) {
                message.setData(CommonConstants.HEARTBEAT_EVENT);
                return message;
            }
            if (dataLength <= 0) {
                return message;
            }
            // 解析数据
            byte[] dataBytes = new byte[dataLength];
            in.readBytes(dataBytes);
            // 解压数据
            dataBytes = CompressorSupport.getCompressor(compressType).decompress(dataBytes);
            // 反序列化对象
            log.info("Codec name: [{}] ", message.getCodec());
            if (messageType == MessageType.RESPONSE.getCode()) {
                // decode response
                try {
                    Object data = decodeResponseData(dataBytes, message.getCodec());
                    message.setData(data);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

            } else {
                // decode request
                try {
                    Object data = decodeRequestData(dataBytes, message.getCodec());
                    message.setData(data);
                } catch (Throwable e) {
                    message.setBroken(true);
                    message.setData(e);
                }
            }

            return message;
        }

        private void checkMagicNum(byte[] magic) {
            if (magic.length != MAGIC_NUM.length) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(magic));
            }
            int len = MAGIC_NUM.length;
            for (int i = 0; i < len; i++) {
                if (magic[i] != MAGIC_NUM[i]) {
                    throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(magic));
                }
            }
        }

        private Object decodeRequestData(byte[] dataBytes, byte deserializeId) {
            return codec.decode(dataBytes, Request.class, deserializeId);
        }

        private Object decodeResponseData(byte[] dataBytes, byte deserializeId) {
            return codec.decode(dataBytes, Response.class, deserializeId);
        }

    }
}
