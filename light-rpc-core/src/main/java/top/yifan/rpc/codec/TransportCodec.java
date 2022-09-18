package top.yifan.rpc.codec;

import top.yifan.rpc.serialize.Serialization;
import top.yifan.rpc.serialize.SerializeSupport;

/**
 * TransportCodec
 * <p>
 * netty传输编解码器
 *
 * @author Star Zheng
 */
public class TransportCodec implements Codec {

    public static final String NANE = "transport";

    @Override
    public byte[] encode(Object data, byte serializeId) {
        Serialization serialization = SerializeSupport.getSerializationById(serializeId);
        return serialization.serialize(data);
    }

    @Override
    public Object decode(byte[] data, Class<?> clazz, byte serializeId) {
        Serialization serialization = SerializeSupport.getSerializationById(serializeId);
        return serialization.deserialize(data, clazz);
    }

}
