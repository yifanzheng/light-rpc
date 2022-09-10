package top.yifan.exception;

/**
 * SerializeException
 *
 * @author Star Zheng
 */
public class SerializeException extends RuntimeException {

    public SerializeException(String message) {
        super(message);
    }

    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
