package top.yifan.rpc.exchange;

import lombok.Data;
import lombok.ToString;

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

    /**
     * 是否解码失败
     */
    private boolean broken = false;

    private Object requestData;

}
