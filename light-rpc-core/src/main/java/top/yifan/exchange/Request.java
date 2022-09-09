package top.yifan.exchange;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Request
 *
 * @author Star Zheng
 */
@Data
@Builder
public class Request implements Serializable {

    private static final long serialVersionUID = -7845678377043292305L;

    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] parameters;
    // TODO 暂时不实现
    private String version;
    private String group;
}
