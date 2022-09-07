package top.yifan.serialize.hession2;

import top.yifan.serialize.Serializer;

/**
 * Hession2Serializer
 *
 * @author Star Zheng
 */
public class Hession2Serializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return null;
    }
}
