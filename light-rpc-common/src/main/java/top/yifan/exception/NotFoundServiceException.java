package top.yifan.exception;

/**
 * NotFoundServiceException
 *
 * @author Star Zheng
 */
public class NotFoundServiceException extends Exception {

    private static final long serialVersionUID = 4291226026980648214L;

    public NotFoundServiceException(String message) {
        super(message);
    }
}
