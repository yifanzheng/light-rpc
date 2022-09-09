package top.yifan.remoting.transport.netty;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import top.yifan.codec.Codec;
import top.yifan.compressor.Compressor;
import top.yifan.constants.CodecConstants;
import top.yifan.constants.CommonConstants;
import top.yifan.constants.CompressType;
import top.yifan.constants.SerializationType;
import top.yifan.exchange.Message;
import top.yifan.extension.ExtensionLoader;

import java.util.Arrays;

/**
 * Netty Codec 适配器，自定义编码协议
 * <pre>
 *      0     1     2     3     4        5    6    7    8        9          10      11    12   13  14  15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+--------+-----+-----+-------+
 *   |   magic   code        |version |    full length      |messageType| codec |compress|    messageId      |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4Byte  magic code（魔法数）   1Byte version（版本）   4Byte full length（消息长度）    1Byte messageType（消息类型）
 * 1Byte codec（序列化类型） 1Byte compress（压缩类型）    4Byte requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 * <p>
 * {@link LengthFieldBasedFrameDecoder} is a length-based decoder , used to solve TCP unpacking and sticking problems.
 * </p>
 *
 * @author Star Zheng
 * @see <a href="https://zhuanlan.zhihu.com/p/95621344">LengthFieldBasedFrameDecoder解码器</a>
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
                // TODO 重写这部分
                out.writeBytes(CodecConstants.MAGIC);
                out.writeByte(CodecConstants.VERSION);
                // leave a place to write the value of full length
                out.writerIndex(out.writerIndex() + 4);
                out.writeByte(message.getMType());
                out.writeByte(message.getCodec());
                out.writeByte(message.getCompress());
                out.writeInt(message.getMId());
                // build full length
                byte[] bodyBytes = null;
                // fullLength = head length + body length
                int fullLength = CodecConstants.HEAD_LENGTH;
                // 如果messageType不是 heartbeat message，则处理数据
                if (message.getMType() != CodecConstants.HEARTBEAT_TYPE) {
                    // serialize the object
                    log.info("codec name: [{}] ", message.getCodec());
                    //bodyBytes = codec.encode(message.getData(), SerializationType.getName(message.getCodec()));
                    bodyBytes = JSON.toJSONBytes(message.getData());
                    // TODO 调整 compress the bytes
                    //String compressName = CompressType.getName(message.getCompress());
                    //Compressor compressor = ExtensionLoader.getExtensionLoader(Compressor.class)
                    //        .getExtension(compressName);
                    //bodyBytes = compressor.compress(bodyBytes);
                    fullLength += bodyBytes.length;
                }

                if (bodyBytes != null) {
                    out.writeBytes(bodyBytes);
                }
                int writeIndex = out.writerIndex();
                // 将索引位置重置到 fullLength 位置处，
                out.writerIndex(writeIndex - fullLength + CodecConstants.MAGIC.length + CodecConstants.VERSION);
                out.writeInt(fullLength);
                out.writerIndex(writeIndex);
            } catch (Exception e) {
                log.error("Encode request error!", e);
            }
        }
    }

    private class InternalDecoder extends LengthFieldBasedFrameDecoder {
        public InternalDecoder() {
            // lengthFieldOffset: magic code is 4 bytes, and version is 1 byte, and then full length. so value is 5
            // lengthFieldLength: full length is 4 byte. so value is 4
            // lengthAdjustment: full length include all data and read 9 bytes before, so the left length is (fullLength-9). so values is -9
            // initialBytesToStrip: we will check magic code and version manually, so do not strip any bytes. so values is 0
            this(CodecConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
        }

        /**
         * @param maxFrameLength      Maximum frame length. It decide the maximum length of data that can be received.
         *                            If it exceeds, the data will be discarded.
         * @param lengthFieldOffset   Length field offset. The length field is the one that skips the specified length of byte.
         * @param lengthFieldLength   The number of bytes in the length field.
         * @param lengthAdjustment    The compensation value to add to the value of the length field
         * @param initialBytesToStrip Number of bytes skipped.
         *                            If you need to receive all of the header+body data, this value is 0
         *                            if you only want to receive the body data, then you need to skip the number of bytes consumed by the header.
         */
        public InternalDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                               int lengthAdjustment, int initialBytesToStrip) {
            super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
        }

        @Override
        protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
            Object decoded = super.decode(ctx, in);
            if (decoded instanceof ByteBuf) {
                ByteBuf frame = (ByteBuf) decoded;
                if (frame.readableBytes() >= CodecConstants.HEAD_LENGTH) {
                    try {
                        return decodeFrame(frame);
                    } catch (Exception e) {
                        // TODO 如果异常了，设置 Bad Request
                        log.error("Decode frame error!", e);
                        throw e;
                    } finally {
                        frame.release();
                    }
                }

            }
            return decoded;
        }

        private Object decodeFrame(ByteBuf in) {
            // 这里必须按顺序读取 ByteBuf
            checkMagicNumber(in);
            checkVersion(in);
            // TODO 重写 Header 获取
            int fullLength = in.readInt();
            byte messageType = in.readByte();
            byte codecType = in.readByte();
            byte compressType = in.readByte();
            int messageId = in.readInt();
            Message message = new Message(messageId, messageType, codecType, compressType);
            if (messageType == CodecConstants.HEARTBEAT_TYPE) {
                message.setData(CommonConstants.HEARTBEAT_EVENT);
                return message;
            }
            int bodyLength = fullLength - CodecConstants.HEAD_LENGTH;
            if (bodyLength > 0) {
                byte[] dataBytes = new byte[bodyLength];
                in.readBytes(dataBytes);
                // decompress the bytes
                //String compressName = CompressType.getName(compressType);
                //Compressor compressor = ExtensionLoader.getExtensionLoader(Compressor.class)
                //        .getExtension(compressName);
                //dataBytes = compressor.decompress(dataBytes);
                // deserialize the object
                log.info("codec name: [{}] ", message.getCodec());
                //Object data = codec.decode(dataBytes, SerializationType.getName(message.getCodec()));

                message.setData(new String(dataBytes));
            }
            return message;

        }

        private void checkVersion(ByteBuf in) {
            // 读取version，并进行比较
            byte version = in.readByte();
            if (version != CodecConstants.VERSION) {
                throw new RuntimeException("version isn't compatible" + version);
            }
        }

        private void checkMagicNumber(ByteBuf in) {
            //
            int len = CodecConstants.MAGIC.length;
            byte[] tmp = new byte[len];
            in.readBytes(tmp);
            for (int i = 0; i < len; i++) {
                if (tmp[i] != CodecConstants.MAGIC[i]) {
                    throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
                }
            }
        }

    }
}
