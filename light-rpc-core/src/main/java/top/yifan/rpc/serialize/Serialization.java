package top.yifan.rpc.serialize;


import top.yifan.constants.SerializationType;
import top.yifan.extension.SPI;

/**
 * 序列化接口，所有序列化类都要实现这个接口
 *
 * @author Star Zheng
 */
@SPI
public interface Serialization {

    byte getSerializeId();

    /**
     * 序列化
     *
     * @param obj 要序列化的对象
     * @return 字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes 序列化后的字节数组
     * @param clazz 目标类
     * @param <T>   类的类型。比如 {@code String.class} 的类型是 {@code Class<String>}.
     *              如果不知道类的类型的话，使用 {@code Class<?>}
     * @return 反序列化的对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
