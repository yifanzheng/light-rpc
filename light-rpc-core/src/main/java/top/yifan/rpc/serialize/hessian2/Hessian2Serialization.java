package top.yifan.rpc.serialize.hessian2;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import top.yifan.constants.SerializationType;
import top.yifan.exception.SerializeException;
import top.yifan.rpc.serialize.Serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian2Serialization
 * <p>
 * Hessian is a dynamically-typed, binary serialization.
 *
 * @author Star Zheng
 */
public class Hessian2Serialization implements Serialization {

    @Override
    public byte getSerializeId() {
        return SerializationType.HESSIAN2.getCode();
    }

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(obj);

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new SerializeException(e.getMessage(), e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            HessianInput hessianInput = new HessianInput(byteArrayInputStream);
            Object o = hessianInput.readObject();
            return clazz.cast(o);
        } catch (IOException e) {
            throw new SerializeException(e.getMessage(), e);
        }
    }
}
