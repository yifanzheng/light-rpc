package top.yifan.rpc.serialize.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SerializationException;
import top.yifan.constants.SerializationType;
import top.yifan.rpc.serialize.Serialization;

import java.io.IOException;

/**
 * JacksonSerialization
 *
 * @author Star Zheng
 */
public class JacksonSerialization implements Serialization {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public byte getSerializeId() {
        return SerializationType.JSON.getCode();
    }

    @Override
    public byte[] serialize(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(obj);
        } catch (IOException e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            return OBJECT_MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }
}
