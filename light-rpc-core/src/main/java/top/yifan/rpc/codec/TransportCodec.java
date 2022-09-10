package top.yifan.rpc.codec;

/**
 * TODO 完善 TransportCodec
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
