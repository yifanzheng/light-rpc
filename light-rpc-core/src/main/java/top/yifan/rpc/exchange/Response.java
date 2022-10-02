package top.yifan.rpc.exchange;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Response
 *
 * @author Star Zheng
 */
@Data
@ToString
public class Response implements Serializable {

    private static final long serialVersionUID = -8916088427074689870L;

    /**
     * ok.
     */
    public static final byte OK = 20;

    /**
     * client side timeout.
     */
    public static final byte CLIENT_TIMEOUT = 30;

    /**
     * server side timeout.
     */
    public static final byte SERVER_TIMEOUT = 31;

    /**
     * channel inactive, directly return the unfinished requests.
     */
    public static final byte CHANNEL_INACTIVE = 35;

    /**
     * request format error.
     */
    public static final byte BAD_REQUEST = 40;

    /**
     * response format error.
     */
    public static final byte BAD_RESPONSE = 50;

    /**
     * service not found.
     */
    public static final byte SERVICE_NOT_FOUND = 60;

    /**
     * service error.
     */
    public static final byte SERVICE_ERROR = 70;

    /**
     * internal client error.
     */
    public static final byte CLIENT_ERROR = 90;

    private byte status = OK;

    private String errorMsg;

    /**
     * response body
     */
    private Object result;

}
