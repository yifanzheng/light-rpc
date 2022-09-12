package top.yifan.exception;

/**
 * @author sz7v
 */
public class ZookeeperException extends RuntimeException {

    private static final long serialVersionUID = -920049364928728505L;

    public ZookeeperException(Throwable cause) {
        super(cause);
    }

    public ZookeeperException(String message) {
        super(message);
    }

    public ZookeeperException(String message, Throwable cause) {
        super(message, cause);
    }
}
