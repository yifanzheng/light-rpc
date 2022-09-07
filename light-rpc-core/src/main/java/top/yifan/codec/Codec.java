package top.yifan.codec;

/**
 * @author Star Zheng
 */
public interface Codec {

    void encode(Object data);

    void decode(byte[] data);
}
