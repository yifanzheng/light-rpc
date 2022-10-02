package top.yifan.rpc.exchange;

import lombok.Data;
import lombok.ToString;
import top.yifan.rpc.serialize.Serialization;

import java.io.Serializable;

/**
 * RequestData
 *
 * @author Star Zheng
 */
@Data
@ToString
public class RequestData implements Serializable {

    private static final long serialVersionUID = -2133278172077516380L;

    private String interfaceName;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] parameters;
    private String version = "";
    private String group = "";

    public String getRpcServiceName() {
        return interfaceName + group + version;
    }
}
