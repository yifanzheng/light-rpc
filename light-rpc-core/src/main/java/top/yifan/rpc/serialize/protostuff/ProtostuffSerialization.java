package top.yifan.rpc.serialize.protostuff;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import top.yifan.constants.SerializationType;
import top.yifan.rpc.serialize.Serialization;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Star Zheng
 */
public class ProtostuffSerialization implements Serialization {
    private static final LinkedBuffer BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    private static final Objenesis objenesis = new ObjenesisStd(true);
    private final Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    @Override
    public byte getSerializeId() {
        return SerializationType.PROTOSTUFF.getCode();
    }

    @Override
    @SuppressWarnings("unchecked")
    public byte[] serialize(Object obj) {
        Class<?> clazz = obj.getClass();
        Schema schema = RuntimeSchema.getSchema(clazz);
        byte[] bytes;
        try {
            bytes = ProtostuffIOUtil.toByteArray(obj, schema, BUFFER);
        } finally {
            BUFFER.clear();
        }
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        T obj = objenesis.newInstance(clazz);
        Schema<T> schema = getSchema(clazz);
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }

    @SuppressWarnings("unchecked")
    private <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) schemaCache.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(clazz);
            if (Objects.nonNull(schema)) {
                schemaCache.put(clazz, schema);
            }
        }
        return schema;
    }

}
