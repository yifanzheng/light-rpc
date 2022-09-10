package top.yifan.rpc.serialize.kyro;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import top.yifan.constants.SerializationType;
import top.yifan.exception.SerializeException;
import top.yifan.rpc.exchange.Request;
import top.yifan.rpc.exchange.Response;
import top.yifan.rpc.serialize.Serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * KyroSerialization
 *
 * @author Star Zheng
 */
public class KyroSerialization implements Serialization {

    /**
     * 由于Kyro不是线程安全的，所以使用ThreadLocal存储
     */
    private final ThreadLocal<Kryo> kyroThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(Response.class);
        kryo.register(Request.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public SerializationType getType() {
        return SerializationType.KYRO;
    }

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kyroThreadLocal.get();
            kryo.writeObject(output, obj);
            kyroThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializeException(e.getMessage(), e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kyroThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kyroThreadLocal.remove();
            return clazz.cast(o);
        } catch (Exception e) {
            throw new SerializeException(e.getMessage(), e);
        }
    }
}
