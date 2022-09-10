package top.yifan.exception;

/**
 * RpcException
 *
 * @author Star Zheng
 */
public class RpcException extends RuntimeException {

    private static final long serialVersionUID = -2100864423106475746L;

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
