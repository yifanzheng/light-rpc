package top.yifan.codec;

import top.yifan.extension.SPI;

/**
 * @author Star Zheng
 */
@SPI
public interface Codec {

    byte[] encode(Object data, String serializeKey);

    Object decode(byte[] data, String serializeKey);
}
