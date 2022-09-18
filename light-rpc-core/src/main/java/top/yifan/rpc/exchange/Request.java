package top.yifan.rpc.exchange;

import lombok.Data;
import lombok.ToString;
import top.yifan.util.IDGeneratorUtil;

import java.io.Serializable;

/**
 * Request
 *
 * @author Star Zheng
 */
@Data
@ToString
public class Request implements Serializable {

    private static final long serialVersionUID = -7845678377043292305L;

    private String requestId;
    private String interfaceName;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] parameters;
    private String version = "";
    private String group = "";

    public Request() {
        this.requestId = IDGeneratorUtil.generateUUID();
    }

    public String getRpcServiceName() {
        return this.getInterfaceName() + group + version;
    }
}
