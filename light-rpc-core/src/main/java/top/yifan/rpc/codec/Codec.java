package top.yifan.rpc.codec;

import top.yifan.extension.SPI;

/**
 * Codec
 *
 * @author Star Zheng
 */
@SPI
public interface Codec {

    byte[] encode(Object data, byte serializeId);

    Object decode(byte[] data, Class<?> clazz, byte serializeId);
}
